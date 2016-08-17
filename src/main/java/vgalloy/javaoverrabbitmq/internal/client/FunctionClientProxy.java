package vgalloy.javaoverrabbitmq.internal.client;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.internal.exception.TimeoutException;
import vgalloy.javaoverrabbitmq.internal.marshaller.impl.GlobalMarshaller;
import vgalloy.javaoverrabbitmq.internal.marshaller.impl.GsonMarshaller;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class FunctionClientProxy<P, R> implements Function<P, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionClientProxy.class);

    private final FunctionQueueDefinition<P, R> functionQueueDefinition;
    private final Connection connection;

    /**
     * Constructor.
     *
     * @param connection              the connection
     * @param functionQueueDefinition the functionQueueDefinition
     */
    public FunctionClientProxy(Connection connection, FunctionQueueDefinition<P, R> functionQueueDefinition) {
        this.functionQueueDefinition = Objects.requireNonNull(functionQueueDefinition);
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public R apply(P parameter) {
        byte[] messageAsByte = GsonMarshaller.INSTANCE.serialize(parameter);
        byte[] resultAsByte = sendRPC(messageAsByte);
        return GlobalMarshaller.INSTANCE.deserialize(functionQueueDefinition.getReturnMessageClass(), resultAsByte);
    }

    /**
     * Send the message and wait for the response.
     *
     * @param messageAsByte the message as byte array
     * @return the response as byte array
     */
    private byte[] sendRPC(byte[] messageAsByte) {
        Channel channel = null;
        long startTimeMillis = System.currentTimeMillis();

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

            long remainingTime = functionQueueDefinition.getTimeout() - (System.currentTimeMillis() - startTimeMillis);
            while (remainingTime > 0) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery(remainingTime);
                if (delivery != null && delivery.getProperties().getCorrelationId().equals(corrId)) {
                    return delivery.getBody();
                }
                remainingTime = functionQueueDefinition.getTimeout() - (System.currentTimeMillis() - startTimeMillis);
            }
            throw new TimeoutException(functionQueueDefinition.getTimeout() + "  msec without valid response");
        } catch (Exception e) {
            throw new JavaOverRabbitException(e);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception e) {
                    throw new JavaOverRabbitException(e);
                }
            }
        }
    }

    /**
     * Get a new Channel for method invocation.
     *
     * @return the new chanel
     * @throws IOException if the channel can not be create
     */
    private Channel createChannel() throws IOException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(functionQueueDefinition.getName(), false, false, false, null);
        return channel;
    }
}
