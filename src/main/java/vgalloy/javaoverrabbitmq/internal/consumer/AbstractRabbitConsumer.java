package vgalloy.javaoverrabbitmq.internal.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.queue.QueueDefinition;
import vgalloy.javaoverrabbitmq.internal.exception.JavaOverRabbitException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 18/08/16.
 */
public abstract class AbstractRabbitConsumer<P> extends QueueingConsumer implements RabbitConsumer {

    private final String consumerTag;

    /**
     * Constructor.
     *
     * @param channel         the channel
     * @param queueDefinition the queue definition
     */
    protected AbstractRabbitConsumer(Channel channel, QueueDefinition<P> queueDefinition) {
        super(Objects.requireNonNull(channel));
        try {
            consumerTag = channel.basicConsume(Objects.requireNonNull(queueDefinition).getName(), false, this);
        } catch (IOException e) {
            throw new JavaOverRabbitException("Can not create consumer", e);
        }
    }

    @Override
    public void close() {
        try {
            getChannel().basicCancel(consumerTag);
            getChannel().close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
