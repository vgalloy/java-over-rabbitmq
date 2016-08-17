package vgalloy.javaoverrabbitmq.internal.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.marshaller.impl.GsonMarshaller;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class ConsumerRabbitConsumerImpl<P> extends AbstractRabbitConsumer<P> {

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
        super(channel, consumerQueueDefinition);
        this.consumerQueueDefinition = Objects.requireNonNull(consumerQueueDefinition);
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        try {
            LOGGER.debug("Received body : {}", body);

            P paramAsObject = GsonMarshaller.INSTANCE.deserialize(consumerQueueDefinition.getParameterMessageClass(), body);
            LOGGER.debug("Received paramAsObject : {}", paramAsObject);
            service.accept(paramAsObject);
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
        LOGGER.debug("basicAck");
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }
}
