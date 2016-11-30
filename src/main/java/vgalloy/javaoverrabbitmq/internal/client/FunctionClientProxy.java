package vgalloy.javaoverrabbitmq.internal.client;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.TimeoutException;
import vgalloy.javaoverrabbitmq.internal.marshaller.impl.ExtendedMarshaller;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class FunctionClientProxy<P, R> extends AbstractClient implements Function<P, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionClientProxy.class);

    private final FunctionQueueDefinition<P, R> functionQueueDefinition;

    /**
     * Constructor.
     *
     * @param functionQueueDefinition the queue definition
     * @param connection              the connection
     */
    public FunctionClientProxy(FunctionQueueDefinition<P, R> functionQueueDefinition, Connection connection) {
        super(functionQueueDefinition, connection);
        this.functionQueueDefinition = Objects.requireNonNull(functionQueueDefinition);
    }

    @Override
    public R apply(P parameter) {
        byte[] messageAsByte = functionQueueDefinition.getMarshaller().serialize(parameter);
        return sendRPC(messageAsByte);
    }

    /**
     * Send the message and wait for the response.
     *
     * @param messageAsByte the message as byte array
     * @return the response as byte array
     */
    private R sendRPC(byte[] messageAsByte) {
        Channel channel = null;

        try {
            channel = createChannel();
            QueueingConsumer consumer = new QueueingConsumer(channel);
            String replyQueueName = channel.queueDeclare().getQueue();
            channel.basicConsume(replyQueueName, true, consumer);

            String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            LOGGER.debug("send {}", messageAsByte);
            channel.basicPublish("", functionQueueDefinition.getName(), props, messageAsByte);

            return getResult(consumer, corrId);
        } catch (Exception e) {
            throw new JavaOverRabbitException(e);
        } finally {
            close(channel);
        }
    }

    /**
     * Get the result.
     *
     * @param consumer the response queue consumer
     * @param corrId   the correlation Id
     * @return the result as an object
     * @throws InterruptedException Interrupted Exception
     */
    private R getResult(QueueingConsumer consumer, String corrId) throws InterruptedException {
        long startTimeMillis = System.currentTimeMillis();
        long remainingTime = functionQueueDefinition.getTimeoutMillis() - (System.currentTimeMillis() - startTimeMillis);
        while (remainingTime > 0) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery(remainingTime);
            if (delivery != null && delivery.getProperties().getCorrelationId().equals(corrId)) {
                boolean isError = delivery.getProperties().getHeaders() != null && delivery.getProperties().getHeaders().containsKey(RabbitConsumerException.ERROR_HEADER);
                return ExtendedMarshaller.deserialize(functionQueueDefinition.getMarshaller(), functionQueueDefinition.getReturnMessageClass(), isError, delivery.getBody());
            }
            remainingTime = functionQueueDefinition.getTimeoutMillis() - (System.currentTimeMillis() - startTimeMillis);
        }
        throw new TimeoutException(functionQueueDefinition.getTimeoutMillis() + "  ms without valid response");
    }
}
