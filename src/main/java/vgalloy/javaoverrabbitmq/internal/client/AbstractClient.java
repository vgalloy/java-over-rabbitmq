package vgalloy.javaoverrabbitmq.internal.client;

import java.io.IOException;
import java.util.Objects;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.api.model.RabbitElement;
import vgalloy.javaoverrabbitmq.api.queue.UntypedQueue;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 18/08/16.
 */
public abstract class AbstractClient implements RabbitElement {

    private final UntypedQueue untypedQueue;
    private final Connection connection;

    /**
     * Constructor.
     *
     * @param untypedQueue the queue name
     * @param connection   the connection
     */
    protected AbstractClient(UntypedQueue untypedQueue, Connection connection) {
        this.untypedQueue = Objects.requireNonNull(untypedQueue);
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
        channel.queueDeclare(untypedQueue.getName(), false, false, false, null);
        return channel;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
            throw new JavaOverRabbitException(e);
        }
    }

    @Override
    public int getMessageCount() {
        Channel channel = null;
        try {
            channel = connection.createChannel();
            AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(untypedQueue.getName(), false, false, false, null);
            return declareOk.getMessageCount();
        } catch (IOException e) {
            throw new JavaOverRabbitException(e);
        } finally {
            close(channel);
        }
    }

    /**
     * Close silently the channel.
     *
     * @param channel the channel to close
     */
    protected static void close(Channel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (Exception e) {
                throw new JavaOverRabbitException(e);
            }
        }
    }
}
