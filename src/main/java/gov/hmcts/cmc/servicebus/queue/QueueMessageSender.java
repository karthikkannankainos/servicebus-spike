package gov.hmcts.cmc.servicebus.queue;

import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.google.gson.Gson;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import gov.hmcts.cmc.servicebus.dto.Claim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import static java.nio.charset.StandardCharsets.*;

import java.time.Duration;
import java.util.*;

@Component
public class QueueMessageSender implements IQueueMessageSender{

    @Value("${cmc.servicebus.connection.url}")
    private String queueConnectionString;

    @Value("${cmc.servicebus.connection.queue.name}")
    private String queueName;

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private JmsTemplate jmsTemplate;

    private static Logger logger = LoggerFactory.getLogger(QueueMessageSender.class);


    private final static Gson GSON = new Gson();

    @PostConstruct
    public void before(){
        if(!Optional.of(queueConnectionString).isPresent()){
            queueConnectionString = "Endpoint=sb://cmc-claim-spike.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=9TM/H+0fZB4szplsQxfd3oBim/oAyCioKoLW8k53uDI=";
            queueName = "cmc.claim.save.bulk.print";
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long sendMessage(Claim claim) throws JMSException {
        logger.debug("The connection string that we received is " + queueConnectionString);

        //jmsTemplate.send(queueName,  (Session session) ->  session.createTextMessage(claim.getClaim()));

        jmsTemplate.convertAndSend(queueName, claim.getClaim(), new MessagePostProcessor() {
            @Override
            public javax.jms.Message postProcessMessage(javax.jms.Message message) throws JMSException {
                message.setJMSCorrelationID("COREE");
                message.setJMSMessageID("Message");
                message.setStringProperty("JMSXGroupID", appName);
                return message;
            }
        });
        //sendClient.close();
        return (long) claim.getId();
    }

    public Long sendMessageXXXX(Claim claim) throws ServiceBusException, InterruptedException {
        logger.debug("The connection string that we received is " + queueConnectionString);
        QueueClient sendClient = new QueueClient(new ConnectionStringBuilder(queueConnectionString, queueName),
                ReceiveMode.PEEKLOCK);

        Message message = new Message(GSON.toJson(claim).getBytes(UTF_8));
        message.setContentType("application/json");
        message.setLabel("claim_data");
        message.setMessageId(UUID.randomUUID().toString());
        message.setTimeToLive(Duration.ofMinutes(2));

        logger.debug("Before sending the message in Async in QueueMessageSender ");
        sendClient.sendAsync(message).thenRunAsync(() -> {
            logger.debug("\n\tMessage acknowledged: Id = %s", message.getMessageId());
        });

        logger.debug("After sending the message in Async in QueueMessageSender ");
        //sendClient.close();
        logger.debug("Closing the senderClient in QueueMessageSender ");
        return (long) claim.getId();
    }


}
