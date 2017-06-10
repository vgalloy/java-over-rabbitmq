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
 * Created by Vincent Galloy on 18/08/16.
 *
 * @author Vincent Galloy
 */
public abstract class AbstractClient implements RabbitElement {

    private final UntypedQueue untypedQueue;
    private final Connection connection;
    private Channel currentChannel;

    /**
     * Constructor.
     *
     * @param untypedQueue the queue name
     * @param connection   the connection
     */
    AbstractClient(UntypedQueue untypedQueue, Connection connection) {
        this.untypedQueue = Objects.requireNonNull(untypedQueue);
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void close() {
        try {
            close(currentChannel);
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
     * Get a an  Channel for method invocation.
     *
     * @return the chanel
     */
    Channel getChannel() {
        if (currentChannel != null) {
            return currentChannel;
        }
        currentChannel = createChannel();
        return currentChannel;
    }

    /**
     * Get a new Channel for method invocation.
     *
     * @return the new chanel
     */
    private Channel createChannel() {
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(untypedQueue.getName(), false, false, false, null);
            return channel;
        } catch (IOException e) {
            throw new JavaOverRabbitException("Unable to create the channel", e);
        }
    }

    /**
     * Close silently the channel.
     *
     * @param channel the channel to close
     */
    private static void close(Channel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (Exception e) {
                throw new JavaOverRabbitException(e);
            }
        }
    }
}
