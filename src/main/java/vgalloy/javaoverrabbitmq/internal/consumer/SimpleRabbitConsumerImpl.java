package vgalloy.javaoverrabbitmq.internal.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.message.RabbitMessage;
import vgalloy.javaoverrabbitmq.api.queue.QueueDefinition;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;
import vgalloy.javaoverrabbitmq.internal.impl.GsonMarshaller;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class SimpleRabbitConsumerImpl<P extends RabbitMessage> extends QueueingConsumer implements RabbitConsumer {

    private final QueueDefinition<P, RabbitMessage.None> queueDefinition;
    private final RPCQueueMethod<P, RabbitMessage.None> service;

    /**
     * Constructor.
     *
     * @param channel         the channel
     * @param queueDefinition the queue definition
     * @param service         the implementation
     */
    public SimpleRabbitConsumerImpl(Channel channel, QueueDefinition<P, RabbitMessage.None> queueDefinition, RPCQueueMethod<P, RabbitMessage.None> service) {
        super(channel);
        this.queueDefinition = Objects.requireNonNull(queueDefinition);
        this.service = Objects.requireNonNull(service);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRabbitConsumerImpl.class);

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        LOGGER.debug("Received body : {}", body);

        P paramAsObject = GsonMarshaller.INSTANCE.deserialize(queueDefinition.getParameterMessageClass(), body);
        LOGGER.debug("Received paramAsObject : {}", paramAsObject);

        service.invoke(paramAsObject);
        LOGGER.debug("basicAck");
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }

    @Override
    public void close() {
        try {
            getChannel().close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
