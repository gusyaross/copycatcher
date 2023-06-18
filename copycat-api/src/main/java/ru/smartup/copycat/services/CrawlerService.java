package ru.smartup.copycat.services;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.smartup.copycat.dao.CrawlerHistoryRepository;
import ru.smartup.copycat.dao.CrawlerStateRepository;
import ru.smartup.copycat.dto.request.CreateCrawlerConfigurationRequest;
import ru.smartup.copycat.dto.request.UpdateCrawlerConfigurationRequest;
import ru.smartup.copycat.dto.response.*;
import ru.smartup.copycat.exceptions.CrawlerAlreadyActiveException;
import ru.smartup.copycat.exceptions.EntityNotFoundException;
import ru.smartup.copycat.exceptions.DuplicateCrawlerNameException;
import ru.smartup.copycat.mappers.CrawlerConfigurationMapper;
import ru.smartup.copycat.mappers.CrawlerHistoryMapper;
import ru.smartup.copycat.exceptions.*;
import ru.smartup.models.*;
import ru.smartup.utils.MessageQueue;
import ru.smartup.utils.S3Storage;
import ru.smartup.utils.exceptions.ErrorCode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 *     CrawlerService is a service to handle user request with crawler
 * </p>*/
@Service
public class CrawlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);
    private final Gson gson = new Gson();
    private final CrawlerStateRepository crawlerStateRepository;
    private final CrawlerHistoryRepository crawlerHistoryRepository;
    private final CrawlerConfigurationMapper crawlerConfigurationMapper;
    private final CrawlerHistoryMapper crawlerHistoryMapper;
    private final S3Storage s3Storage;
    private final MessageQueue messageQueue;

    public CrawlerService(CrawlerStateRepository crawlerStateRepository, CrawlerHistoryRepository crawlerHistoryRepository, CrawlerConfigurationMapper crawlerConfigurationMapper,
                          CrawlerHistoryMapper crawlerHistoryMapper, S3Storage s3Storage, MessageQueue messageQueue) {
        this.crawlerStateRepository = crawlerStateRepository;
        this.crawlerHistoryRepository = crawlerHistoryRepository;
        this.crawlerConfigurationMapper = crawlerConfigurationMapper;
        this.crawlerHistoryMapper = crawlerHistoryMapper;
        this.s3Storage = s3Storage;
        this.messageQueue = messageQueue;
    }

    /**
     * <p>
     *     createCrawlerConfiguration creates configuration of user crawler and then storing it as json object in s3 storage
     * </p>
     *
     * @param request
     *      User request to create crawler configuration
     * @throws DuplicateCrawlerNameException
     *      In case there is another crawler with same name
     * @return Response with data entered by user and id of his crawler*/
    public CreateCrawlerConfigurationResponse createCrawlerConfiguration(CreateCrawlerConfigurationRequest request) throws DuplicateCrawlerNameException {
        CrawlerConfiguration configuration = crawlerConfigurationMapper.requestToModel(request);
        String jsonConfiguration = gson.toJson(configuration);
        CrawlerState state = new CrawlerState(
                configuration.getName(),
                StateType.INACTIVE,
                new ArrayList<>()
        );

        try {
            crawlerStateRepository.save(state);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause().getCause().getMessage().contains("duplicate")) {
                throw new DuplicateCrawlerNameException(ex);
            }
        }
        s3Storage.putObject(state.getCrawlerName(), jsonConfiguration);

        CreateCrawlerConfigurationResponse response = crawlerConfigurationMapper.modelToResponse(configuration);
        response.setId(state.getId());
        LOGGER.info("Crawler with name " + configuration.getName() + " successfully created");
        return response;
    }

    /**
     * <p>
     *     startCrawler starts crawler by getting its configuration to put link in the message queue to processing
     * </p>
     *
     * @param crawlerName
     *      A name of crawler user wants to start
     * @throws UnableToStartCrawlerException
     *      In case crawler cannot be started */
    public void startCrawler(String crawlerName) throws UnableToStartCrawlerException {
        try {
            Optional<CrawlerState> crawlerStateOptional = crawlerStateRepository.findByCrawlerNameEquals(crawlerName);
            if (crawlerStateOptional.isEmpty()) {
                throw new EntityNotFoundException(ErrorCode.CRAWLER_NOT_FOUND);
            }
            CrawlerConfiguration crawlerConfiguration = gson.fromJson(s3Storage.getObject(crawlerName), CrawlerConfiguration.class);
            CrawlerState crawlerState = crawlerStateOptional.get();
            if (crawlerState.getStatus() == StateType.ACTIVE) {
                throw new CrawlerAlreadyActiveException();
            }
            crawlerState.setStatus(StateType.ACTIVE);
            CrawlerHistory crawlerHistory = new CrawlerHistory(crawlerState, LocalDateTime.now(), crawlerConfiguration.getStartingPoints().size(), HistoryStatus.IN_PROGRESS);
            crawlerState.addHistory(crawlerHistory);
            crawlerStateRepository.save(crawlerState);
            List<String> messages = new ArrayList<>();
            int messageBytesSize = 0;
            StringBuilder stringBuilder = new StringBuilder();
            for (String startingPoint : crawlerConfiguration.getStartingPoints()) {
                messageBytesSize += startingPoint.getBytes().length + 1;
                if (messageBytesSize >= messageQueue.getMessageSizeLimit()) {
                    messageBytesSize = startingPoint.getBytes().length + 1;
                    messages.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder(crawlerName);
                }
                stringBuilder.append(String.format("%s,", startingPoint));
            }
            messages.add(stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString());
            for (String message : messages) {
                messageQueue.putMessage(crawlerName, message);
            }
        } catch (Throwable ex) {
            throw new UnableToStartCrawlerException(ex);
        }
    }

    /**
     * <p>
     *     updateCrawlerConfiguration updates configuration of user crawler
     * </p>
     *
     * @param request
     *      user request with crawler data he wants to change
     * @param crawlerName
     *      A name of crawler user wants to update
     *
     * @return response with updated crawler data
     *
     * @throws EntityNotFoundException
     *      In case crawler with specified id has not been found*/
    public UpdateCrawlerConfigurationResponse updateCrawlerConfiguration(UpdateCrawlerConfigurationRequest request, String crawlerName) throws EntityNotFoundException {
        CrawlerConfiguration configuration = new CrawlerConfiguration(crawlerName, request.getStartingPoints());
        String jsonConfiguration = gson.toJson(configuration);

        Optional<CrawlerState> stateOptional = crawlerStateRepository.findByCrawlerNameEquals(crawlerName);
        if (stateOptional.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.CRAWLER_NOT_FOUND);
        }

        s3Storage.putObject(stateOptional.get().getCrawlerName(), jsonConfiguration);

        UpdateCrawlerConfigurationResponse response = new UpdateCrawlerConfigurationResponse(request.getStartingPoints());

        LOGGER.info("Crawler with name " + configuration.getName() + " successfully updated");
        return response;
    }

    /**
     * <p>
     *     getCrawlerConfiguration getting crawler configuration by crawler name
     * </p>
     *
     * @param crawlerName
     *      A name of crawler user wants to get
     *
     * @return response with crawler data
     *
     * @throws EntityNotFoundException
     *      In case crawler state or crawler history has not been found */
    public GetCrawlerConfigurationResponse getCrawlerConfiguration(String crawlerName) throws IOException, EntityNotFoundException {
        Optional<CrawlerState> stateOptional = crawlerStateRepository.findByCrawlerNameEquals(crawlerName);
        if (stateOptional.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.CRAWLER_NOT_FOUND);
        }

        Optional<CrawlerHistory> lastRunOptional = crawlerHistoryRepository.findTopByCrawlerStateOrderByStartTimeDesc(stateOptional.get());
        if (lastRunOptional.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.CRAWLER_HISTORY_NOT_FOUND);
        }

        CrawlerHistoryResponse historyResponse = crawlerHistoryMapper.modelToResponse(lastRunOptional.get());
        CrawlerConfiguration configuration = gson.fromJson(s3Storage.getObject(crawlerName), CrawlerConfiguration.class);

        GetCrawlerConfigurationResponse response = new GetCrawlerConfigurationResponse(crawlerName, configuration.getStartingPoints(),
                stateOptional.get().getStatus(), historyResponse);

        LOGGER.info("Crawler with name " + configuration.getName() + " successfully got");

        return response;
    }

    /**
     * <p>
     *     getCrawlerConfigurations getting crawler configurations by crawler status
     * </p>
     *
     * @param crawlerStatus - optional param
     *
     * @return response with crawlers data
     */
    public GetCrawlerConfigurationsResponse getCrawlerConfigurations(StateType crawlerStatus) throws IOException {
        List<CrawlerState> states = new ArrayList<>();
        if (crawlerStatus == null) {
            crawlerStateRepository.findAll().forEach(states::add);
        } else {
            states.addAll(crawlerStateRepository.findAllByStatus(crawlerStatus));
        }

        List<GetCrawlerConfigurationResponse> configurations = new ArrayList<>();
        for (CrawlerState state : states) {
            Optional<CrawlerHistory> lastRunOptional = crawlerHistoryRepository.findTopByCrawlerStateOrderByStartTimeDesc(state);
            CrawlerHistoryResponse historyResponse = null;
            if (lastRunOptional.isPresent()) {
                historyResponse = crawlerHistoryMapper.modelToResponse(lastRunOptional.get());
            }

            CrawlerConfiguration configuration = gson.fromJson(s3Storage.getObject(state.getCrawlerName()), CrawlerConfiguration.class);

            configurations.add(new GetCrawlerConfigurationResponse(state.getCrawlerName(), configuration.getStartingPoints(),
                    state.getStatus(), historyResponse));
        }

        return new GetCrawlerConfigurationsResponse(configurations);
    }
}