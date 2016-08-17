package vgalloy.javaoverrabbitmq.internal.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.impl.GsonMarshaller;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class ConsumerRabbitConsumerImpl<P> extends QueueingConsumer implements RabbitConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerRabbitConsumerImpl.class);
    private final ConsumerQueueDefinition<P> consumerQueueDefinition;
    private final Consumer<P> service;

    /**
     * Constructor.
     *
     * @param channel                 the channel
     * @param consumerQueueDefinition the queue definition
     * @param service                 the implementation
     */
    public ConsumerRabbitConsumerImpl(Channel channel, ConsumerQueueDefinition<P> consumerQueueDefinition, Consumer<P> service) {
        super(channel);
        this.consumerQueueDefinition = Objects.requireNonNull(consumerQueueDefinition);
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        LOGGER.debug("Received body : {}", body);

        P paramAsObject = GsonMarshaller.INSTANCE.deserialize(consumerQueueDefinition.getParameterMessageClass(), body);
        LOGGER.debug("Received paramAsObject : {}", paramAsObject);

        service.accept(paramAsObject);
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
