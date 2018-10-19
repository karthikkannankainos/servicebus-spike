package gov.hmcts.cmc.servicebus.queue;

import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import gov.hmcts.cmc.servicebus.dto.Claim;

import javax.jms.JMSException;

public interface IQueueMessageSender {

    Long sendMessage(Claim type) throws  JMSException;

    Long sendTopicMessage(Claim claim) throws JMSException;
}
