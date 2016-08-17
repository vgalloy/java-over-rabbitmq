package vgalloy.javaoverrabbitmq.internal.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.impl.GsonMarshaller;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class FunctionRabbitConsumerImpl<P, R> extends QueueingConsumer implements RabbitConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionRabbitConsumerImpl.class);

    private final FunctionQueueDefinition<P, R> functionQueueDefinition;
    private final Function<P, R> service;

    /**
     * Constructor.
     *
     * @param channel                 the channel
     * @param functionQueueDefinition the functionQueueDefinition
     * @param service                 the service implementation
     */
    public FunctionRabbitConsumerImpl(Channel channel, FunctionQueueDefinition<P, R> functionQueueDefinition, Function<P, R> service) {
        super(channel);
        this.functionQueueDefinition = Objects.requireNonNull(functionQueueDefinition);
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        LOGGER.debug("Received body : {}", body);

        P paramAsObject = GsonMarshaller.INSTANCE.deserialize(functionQueueDefinition.getParameterMessageClass(), body);
        LOGGER.debug("Received paramAsObject : {}", paramAsObject);

        R result = service.apply(paramAsObject);

        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                .Builder()
                .correlationId(properties.getCorrelationId())
                .build();

        byte[] resultAsByte = GsonMarshaller.INSTANCE.serialize(result);

        getChannel().basicPublish("", properties.getReplyTo(), replyProps, resultAsByte);
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
