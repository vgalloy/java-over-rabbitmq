package vgalloy.javaoverrabbitmq.internal.consumer;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;

import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.api.model.RabbitElement;
import vgalloy.javaoverrabbitmq.api.queue.UntypedQueue;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 18/08/16.
 */
public abstract class AbstractRabbitConsumer extends DefaultConsumer implements RabbitElement {

    private final String consumerTag;
    private final UntypedQueue untypedQueue;

    /**
     * Constructor.
     *
     * @param channel      the channel
     * @param untypedQueue the queue name
     */
    protected AbstractRabbitConsumer(Channel channel, UntypedQueue untypedQueue) {
        super(Objects.requireNonNull(channel));
        this.untypedQueue = Objects.requireNonNull(untypedQueue);
        try {
            consumerTag = channel.basicConsume(untypedQueue.getName(), false, this);
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

    @Override
    public int getMessageCount() {
        try {
            AMQP.Queue.DeclareOk declareOk = getChannel().queueDeclare(untypedQueue.getName(), false, false, false, null);
            return declareOk.getMessageCount();
        } catch (IOException e) {
            throw new JavaOverRabbitException("Unable to get the message count", e);
        }
    }
}
