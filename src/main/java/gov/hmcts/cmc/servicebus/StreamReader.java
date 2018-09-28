package gov.hmcts.cmc.servicebus;

public class StreamReader {
    public static String readStream(byte[] bytes) {
        return new String(bytes);
    }
}
