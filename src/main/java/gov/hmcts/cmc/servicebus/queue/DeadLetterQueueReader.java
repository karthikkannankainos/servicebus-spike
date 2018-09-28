package gov.hmcts.cmc.servicebus.queue;

import com.google.gson.Gson;
import com.microsoft.azure.servicebus.ClientFactory;
import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.SubscriptionClient;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.MessagingFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public class DeadLetterQueueReader {
    private final static Gson GSON = new Gson();

    public static void main(String args[]) throws Exception {
        // Create a QueueClient instance and then asynchronously send messages.
        // Close the sender once the send operation is complete.

        String connectionString = "Endpoint=sb://cmc-claim-spike.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=9TM/H+0fZB4szplsQxfd3oBim/oAyCioKoLW8k53uDI=";
        String deadLetterQueueName = "cmc.claim.save.bulk.print/$deadletterqueue";
        ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(connectionString, deadLetterQueueName);


        MessagingFactory factory = MessagingFactory.createFromConnectionStringBuilder(connectionStringBuilder);

        QueueClient queueClient  = new QueueClient(connectionStringBuilder, ReceiveMode.PEEKLOCK);



        queueClient.registerMessageHandler(new IMessageHandler() {
            @Override
            public CompletableFuture<Void> onMessageAsync(IMessage message) {
                if (message.getLabel() != "claim_data") {
                    System.out.println("The message received is not of label claim data");
                }
                Optional.of(message).ifPresent(msg -> {
                            System.out.println(msg.getContentType());
                            System.out.println(msg.getMessageId());
                            System.out.println(msg.getBody());
                        }

                );
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public void notifyException(Throwable exception, ExceptionPhase phase) {
                System.out.println("Exception happened in the  message handler" + exception.getStackTrace());
                System.out.println("Failure happened in the phase " + phase.name());
            }
        });

    }
}
