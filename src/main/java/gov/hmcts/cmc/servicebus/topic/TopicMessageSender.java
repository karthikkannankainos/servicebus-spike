package gov.hmcts.cmc.servicebus.topic;


import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.google.gson.Gson;
import static java.nio.charset.StandardCharsets.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import org.apache.commons.cli.*;

public class TopicMessageSender {
    static final Gson GSON = new Gson();


    public static void main(String args[]) throws Exception {

        TopicClient sendClient;
        SubscriptionClient subscription1Client;
        SubscriptionClient subscription2Client;
        SubscriptionClient subscription3Client;

        String connectionString = "Endpoint=sb://cmc-claim-spike.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=9TM/H+0fZB4szplsQxfd3oBim/oAyCioKoLW8k53uDI=";


        // Create a QueueClient instance using the connection string builder
        // We set the receive mode to "PeekLock", meaning the message is delivered
        // under a lock and must be acknowledged ("completed") to be removed from the queue
        //subscription1Client = new SubscriptionClient(new ConnectionStringBuilder(connectionString, "cmc-claim-save/subscriptions/bulk-print"), ReceiveMode.PEEKLOCK);
        //subscription2Client = new SubscriptionClient(new ConnectionStringBuilder(connectionString, "cmc-claim-save/subscriptions/Subscription2"), ReceiveMode.PEEKLOCK);
        //subscription3Client = new SubscriptionClient(new ConnectionStringBuilder(connectionString, "cmc-claim-save/subscriptions/Subscription3"), ReceiveMode.PEEKLOCK);

        // registerMessageHandlerOnClient(subscription1Client);
         //registerMessageHandlerOnClient(subscription2Client);
         //registerMessageHandlerOnClient(subscription3Client);

        sendClient = new TopicClient(new ConnectionStringBuilder(connectionString, "cmc-claim-save"));

        String data = "{\"message\":\"Claim created with number 23456\"}";

        Message message = new Message(data);
        message.setContentType("application/json");
        message.setLabel("claim_data");
        message.setMessageId("23456");
        message.setTimeToLive(Duration.ofMinutes(2));
        System.out.printf("Topic Message sending: Id = %s\n", message.getMessageId());
        sendClient.sendAsync(message).thenRunAsync(() -> {
            System.out.printf("\tTopic Message acknowledged: Id = %s\n", message.getMessageId());
        });
        sendClient.close();
        //sendMessagesAsync(sendClient).thenRunAsync(() -> sendClient.closeAsync());

        // wait for ENTER or 10 seconds elapsing
        //waitForEnter(60);

        /*CompletableFuture.allOf(
                subscription1Client.closeAsync(),
                subscription2Client.closeAsync(),
                subscription3Client.closeAsync()).join();*/
    }


    private static void waitForEnter(int seconds) {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            executor.invokeAny(Arrays.asList(() -> {
                System.in.read();
                return 0;
            }, () -> {
                Thread.sleep(seconds * 1000);
                return 0;
            }));
        } catch (Exception e) {
            // absorb
        }
    }
}
