package vgalloy.javaoverrabbitmq.internal.consumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class FunctionRabbitConsumerImpl<P, R> extends AbstractRabbitConsumer {

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
        super(channel, functionQueueDefinition);
        this.functionQueueDefinition = Objects.requireNonNull(functionQueueDefinition);
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {

        try {
            LOGGER.debug("Received body : {}", body);

            P paramAsObject = functionQueueDefinition.getMarshaller().deserialize(functionQueueDefinition.getParameterMessageClass(), body);
            LOGGER.debug("Received paramAsObject : {}", paramAsObject);

            R result = service.apply(paramAsObject);
            byte[] resultAsByte = functionQueueDefinition.getMarshaller().serialize(result);

            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                    .correlationId(properties.getCorrelationId())
                    .build();
            getChannel().basicPublish("", properties.getReplyTo(), replyProps, resultAsByte);
        } catch (Exception e) {
            LOGGER.error("{}", e);
            Map<String, Object> map = new HashMap<>();
            map.put(RabbitConsumerException.ERROR_HEADER, true);
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                    .headers(map)
                    .correlationId(properties.getCorrelationId())
                    .build();
            byte[] resultAsByte = functionQueueDefinition.getMarshaller().serialize(new RabbitConsumerException(e.getMessage()));
            getChannel().basicPublish("", properties.getReplyTo(), replyProps, resultAsByte);
        }
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }
}
