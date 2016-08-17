package vgalloy.javaoverrabbitmq.internal.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import vgalloy.javaoverrabbitmq.api.queue.QueueDefinition;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 18/08/16.
 */
public abstract class AbstractClient<P> {

    private final QueueDefinition<P> queueDefinition;
    private final Connection connection;

    /**
     * Constructor.
     *
     * @param queueDefinition the queue definition
     * @param connection      the connection
     */
    protected AbstractClient(QueueDefinition<P> queueDefinition, Connection connection) {
        this.queueDefinition = Objects.requireNonNull(queueDefinition);
        this.connection = Objects.requireNonNull(connection);
    }

    /**
     * Get a new Channel for method invocation.
     *
     * @return the new chanel
     * @throws IOException if the channel can not be create
     */
    protected Channel createChannel() throws IOException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueDefinition.getName(), false, false, false, null);
        return channel;
    }
}
