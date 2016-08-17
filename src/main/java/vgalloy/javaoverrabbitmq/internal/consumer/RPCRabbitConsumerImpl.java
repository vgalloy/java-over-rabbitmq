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
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class RPCRabbitConsumerImpl<P extends RabbitMessage, R extends RabbitMessage> extends QueueingConsumer implements RabbitConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RPCRabbitConsumerImpl.class);

    private final QueueDefinition<P, R> queueDefinition;
    private final RPCQueueMethod<P, R> service;

    /**
     * Constructor.
     *
     * @param channel         the channel
     * @param queueDefinition the queueDefinition
     * @param service         the service implementation
     */
    public RPCRabbitConsumerImpl(Channel channel, QueueDefinition<P, R> queueDefinition, RPCQueueMethod<P, R> service) {
        super(channel);
        this.queueDefinition = Objects.requireNonNull(queueDefinition);
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        LOGGER.debug("Received body : {}", body);

        P paramAsObject = GsonMarshaller.INSTANCE.deserialize(queueDefinition.getParameterMessageClass(), body);
        LOGGER.debug("Received paramAsObject : {}", paramAsObject);

        R result = service.invoke(paramAsObject);

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
