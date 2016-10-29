package vgalloy.javaoverrabbitmq.internal.consumer;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.api.queue.UntypedQueue;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 18/08/16.
 */
public abstract class AbstractRabbitConsumer extends QueueingConsumer implements RabbitConsumer {

    private final String consumerTag;

    /**
     * Constructor.
     *
     * @param channel      the channel
     * @param untypedQueue the queue name
     */
    protected AbstractRabbitConsumer(Channel channel, UntypedQueue untypedQueue) {
        super(Objects.requireNonNull(channel));
        try {
            consumerTag = channel.basicConsume(Objects.requireNonNull(untypedQueue).getName(), false, this);
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
            throw new JavaOverRabbitException("Can not close consumer", e);
        }
    }
}
