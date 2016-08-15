package vgalloy.javaoverrabbitmq.api.fake.util;

import com.rabbitmq.client.ConnectionFactory;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class Utils {

    private Utils() {
        throw new IllegalStateException();
    }

    public static ConnectionFactory getConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(29003);
        factory.setUsername("root");
        factory.setPassword("root");
        return factory;
    }
}
