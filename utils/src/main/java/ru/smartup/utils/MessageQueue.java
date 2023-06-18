package ru.smartup.utils;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.springframework.stereotype.Component;
import ru.smartup.utils.config.CloudConfig;
import ru.smartup.utils.config.MessageQueueConfig;

import java.util.List;

/**
 * <p>
 *     MessageQueue is a sqs client wrapper which is made to do operations with message queue from yandex cloud
 * </p>
 * <p>
 *     Sqs client interact with queue sending and receiving messages
 * </p>
 *
 * @see Message
 * @see AmazonSQS*/
@Component
public class MessageQueue {
    private final AmazonSQS sqsClient;
    private final MessageQueueConfig mqConfig;
    public MessageQueue(CloudConfig cloudConfig, AmazonSQS sqsClient) {
        mqConfig = cloudConfig.getMq();
        this.sqsClient = sqsClient;
    }

    /**
     * <p>
     *     GetMessagesByGroupId is a method to get messages with attribute MessageGroupId equals to messageGroupId in method signature
     *     This method gets first 10 messages and filters them to get only messages with specified group id
     * </p>
     *
     * @param messageGroupId
     *      A group id by which we want to get messages from queue
     * @return A list of messages with wanted group id*/
    public List<Message> getMessagesByGroupId(String messageGroupId) {
        String queueUrl = sqsClient.getQueueUrl(mqConfig.getQueueName()).getQueueUrl();
        ReceiveMessageResult result = sqsClient.receiveMessage(new ReceiveMessageRequest().withQueueUrl(queueUrl).withWaitTimeSeconds(mqConfig.getPollDelay()).withAttributeNames("MessageGroupId"));
        return result.getMessages().stream().filter(m -> m.getAttributes().get("MessageGroupId").equals(messageGroupId)).toList();
    }

    /**
     * <p>
     *     PutMessage puts message to queue and assign group id to it
     * </p>
     *
     * @param messageGroupId
     *      A group id of message
     * @param message
     *      text message to push in queue*/
    public void putMessage(String messageGroupId, String message) {
        String queueUrl = sqsClient.getQueueUrl(mqConfig.getQueueName()).getQueueUrl();
        sqsClient.sendMessage(new SendMessageRequest().withQueueUrl(queueUrl).withMessageGroupId(messageGroupId).withMessageBody(message));
    }

    /**
     * <p>
     *     DeleteMessage deletes message from queue
     * </p>
     *
     * @param receiptHandle
     *      Specified id with expire time by which we can find message in queue to delete it*/
    public void deleteMessage(String receiptHandle) {
        String queueUrl = sqsClient.getQueueUrl(mqConfig.getQueueName()).getQueueUrl();
        sqsClient.deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl).withReceiptHandle(receiptHandle));
    }

    public int getMessageSizeLimit() {
        return mqConfig.getMessageSizeLimit();
    }
}