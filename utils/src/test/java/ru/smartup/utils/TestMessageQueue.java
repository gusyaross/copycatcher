package ru.smartup.utils;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import ru.smartup.utils.config.CloudConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CloudConfig.class})
@ConfigurationPropertiesScan("classpath:application.properties")
public class TestMessageQueue {
    @Autowired
    private CloudConfig cloudConfig;
    private final AmazonSQS client = Mockito.mock(AmazonSQS.class);

    private MessageQueue messageQueue;

    private final String queueUrl = "https://mock.queue-url.com";

    @BeforeEach
    public void setUp() {
        messageQueue = new MessageQueue(cloudConfig, client);
        when(client.getQueueUrl(cloudConfig.getMq().getQueueName())).thenReturn(new GetQueueUrlResult().withQueueUrl(queueUrl));
    }

    @Test
    public void testPutMessage() {
        messageQueue.putMessage("groupId", "testMessage");
        verify(client).getQueueUrl(cloudConfig.getMq().getQueueName());
        verify(client).sendMessage(any());
    }

    @Test
    public void testDeleteMessage() {
        when(client.receiveMessage(new ReceiveMessageRequest().withQueueUrl(queueUrl))).thenReturn(new ReceiveMessageResult().withMessages(new Message().withMessageId("1").withBody("testMessage").addAttributesEntry("MessageGroupId", "groupId").withReceiptHandle("EAsgu6Kfw_osKAE")));

        messageQueue.putMessage("groupId", "testMessage");
        messageQueue.deleteMessage("EAsgu6Kfw_osKAE");
        verify(client, times(2)).getQueueUrl(cloudConfig.getMq().getQueueName());
        verify(client).deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl).withReceiptHandle("EAsgu6Kfw_osKAE"));
    }

    @Test
    public void testGetMessages() {
        Message messageGroup1 = new Message().withMessageId("1").withBody("testMessage_group1").addAttributesEntry("MessageGroupId", "group1").withReceiptHandle("EAsgu6Kfw_osKAE");
        Message messageGroup2 = new Message().withMessageId("2").withBody("testMessage_group2").addAttributesEntry("MessageGroupId", "group2").withReceiptHandle("EAsgu6Kfw_osKAF");
        when(client.receiveMessage(new ReceiveMessageRequest().withQueueUrl(queueUrl).withWaitTimeSeconds(cloudConfig.getMq().getPollDelay()).withAttributeNames("MessageGroupId"))).thenReturn(new ReceiveMessageResult().withMessages(messageGroup1, messageGroup2));

        List<Message> messageListGroup1 = messageQueue.getMessagesByGroupId("group1");
        assertEquals(1, messageListGroup1.size());
        assertEquals("1", messageListGroup1.get(0).getMessageId());
        assertEquals("testMessage_group1", messageListGroup1.get(0).getBody());
        assertEquals("EAsgu6Kfw_osKAE", messageListGroup1.get(0).getReceiptHandle());

        List<Message> messageListGroup2 = messageQueue.getMessagesByGroupId("group2");
        assertEquals(1, messageListGroup2.size());
        assertEquals("2", messageListGroup2.get(0).getMessageId());
        assertEquals("testMessage_group2", messageListGroup2.get(0).getBody());
        assertEquals("EAsgu6Kfw_osKAF", messageListGroup2.get(0).getReceiptHandle());
    }
}
