package vgalloy.javaoverrabbitmq.utils.util;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
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
        factory.setVirtualHost("default");
        factory.setPort(29003);
        return factory;
    }

    public static Broker startEmbeddedBroker() {
        BrokerOptions configuration = new BrokerOptions();
        configuration.setConfigProperty("qpid.work_dir", "target/qpid_workdir");
        configuration.setConfigProperty("qpid.amqp_port", "29003");
        configuration.setConfigProperty("qpid.http_port", "30005");
        configuration.setInitialConfigurationLocation("src/test/resources/qpid/qpid-config.json");
        configuration.setConfigProperty("qpid.pass_file", "src/test/resources/qpid/passwd.properties");
        Broker broker = new Broker();
        try {
            broker.startup(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return broker;
    }
}
