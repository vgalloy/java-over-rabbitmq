package vgalloy.javaoverrabbitmq.utils.util;

import com.rabbitmq.client.ConnectionFactory;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class BrokerUtils {

    private BrokerUtils() {
        throw new IllegalStateException();
    }

    public static ConnectionFactory getConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPassword("root");
        factory.setUsername("root");
        factory.setPort(29002);
        return factory;
    }
}
