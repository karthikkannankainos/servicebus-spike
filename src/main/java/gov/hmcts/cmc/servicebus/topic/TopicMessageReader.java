package gov.hmcts.cmc.servicebus.topic;


import com.google.gson.Gson;
import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.MessageHandlerOptions;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.SubscriptionClient;
import com.microsoft.azure.servicebus.TopicClient;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import gov.hmcts.cmc.servicebus.StreamReader;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TopicMessageReader {
    static final Gson GSON = new Gson();


    public static void main(String args[]) throws Exception {

        SubscriptionClient subscription1Client;
        SubscriptionClient subscription2Client;
        SubscriptionClient subscription3Client;

        String connectionString = "Endpoint=sb://cmc-claim-spike.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=9TM/H+0fZB4szplsQxfd3oBim/oAyCioKoLW8k53uDI=";


        // Create a QueueClient instance using the connection string builder
        // We set the receive mode to "PeekLock", meaning the message is delivered
        // under a lock and must be acknowledged ("completed") to be removed from the queue
        subscription1Client = new SubscriptionClient(new ConnectionStringBuilder(connectionString, "cmc-claim-save/subscriptions/bulk-print"), ReceiveMode.PEEKLOCK);
        subscription2Client = new SubscriptionClient(new ConnectionStringBuilder(connectionString, "cmc-claim-save/subscriptions/caseman-send"), ReceiveMode.PEEKLOCK);
        subscription3Client = new SubscriptionClient(new ConnectionStringBuilder(connectionString, "cmc-claim-save/subscriptions/email-send"), ReceiveMode.PEEKLOCK);

        registerMessageHandlerOnClient(subscription1Client);
        registerMessageHandlerOnClient(subscription2Client);
        registerMessageHandlerOnClient(subscription3Client);

        waitForEnter(360);

        subscription1Client.closeAsync();
        subscription2Client.closeAsync();
        subscription3Client.closeAsync();
        //sendMessagesAsync(sendClient).thenRunAsync(() -> sendClient.closeAsync());

        // wait for ENTER or 10 seconds elapsing
        //waitForEnter(60);

        /*CompletableFuture.allOf(
                subscription1Client.closeAsync(),
                subscription2Client.closeAsync(),
                subscription3Client.closeAsync()).join();*/
    }

    static CompletableFuture<Void> sendMessagesAsync(TopicClient sendClient) {


        return CompletableFuture.completedFuture(null);
    }

    static void registerMessageHandlerOnClient(SubscriptionClient receiveClient) throws Exception {

        // register the RegisterMessageHandler callback
        receiveClient.registerMessageHandler(
                new IMessageHandler() {
                    // callback invoked when the message handler loop has obtained a message
                    public CompletableFuture<Void> onMessageAsync(IMessage message) {
                        // receives message is passed to callback
                        if (message.getLabel() != null &&
                                message.getContentType() != null &&
                                message.getLabel().contentEquals("claim_data") &&
                                message.getContentType().contentEquals("application/json")) {

                            byte[] body = message.getBody();

                            System.out.printf(
                                    "\n\t\t\t\t%s Message received: \n\t\t\t\t\t\tMessageId = %s, \n\t\t\t\t\t\tSequenceNumber = %s, \n\t\t\t\t\t\tEnqueuedTimeUtc = %s," +
                                            "\n\t\t\t\t\t\tExpiresAtUtc = %s, \n\t\t\t\t\t\tContentType = \"%s\",  \n\t\t\t\t\t\tContent: [ %s ]\n",
                                    receiveClient.getEntityPath(),
                                    message.getMessageId(),
                                    message.getSequenceNumber(),
                                    message.getEnqueuedTimeUtc(),
                                    message.getExpiresAtUtc(),
                                    message.getContentType(),
                                    StreamReader.readStream(message.getBody()));
                        }
                        return receiveClient.completeAsync(message.getLockToken());
                    }

                    // callback invoked when the message handler has an exception to report
                    public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
                        System.out.printf(exceptionPhase + "-" + throwable.getMessage());
                    }
                },
                // 1 concurrent call, messages are auto-completed, auto-renew duration
                new MessageHandlerOptions(1, false, Duration.ofMinutes(1)));

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
