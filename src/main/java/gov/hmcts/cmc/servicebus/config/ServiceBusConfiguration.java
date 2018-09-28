package gov.hmcts.cmc.servicebus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class ServiceBusConfiguration {

    @Value("${azure.servicebus.host}")
    private String host;

    @Value("${azure.servicebus.sharedaccesskeyname}")
    private String userName;

    @Value("${azure.servicebus.sharedaccesskey}")
    private String password;

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getUrlString() throws UnsupportedEncodingException {
        return String.format("amqps://%1s?amqp.idleTimeout=3600000", host);
    }
}
