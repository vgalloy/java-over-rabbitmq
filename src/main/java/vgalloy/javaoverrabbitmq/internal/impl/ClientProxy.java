package vgalloy.javaoverrabbitmq.internal.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vgalloy.javaoverrabbitmq.api.message.RabbitMessage;
import vgalloy.javaoverrabbitmq.api.queue.QueueDefinition;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class ClientProxy<P extends RabbitMessage, R extends RabbitMessage> implements RPCQueueMethod<P, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProxy.class);

    private final QueueDefinition<P, R> queueDefinition;
    private final Connection connection;

    /**
     * Constructor.
     *
     * @param connection      the connection
     * @param queueDefinition the queueDefinition
     */
    public ClientProxy(Connection connection, QueueDefinition<P, R> queueDefinition) {
        this.queueDefinition = Objects.requireNonNull(queueDefinition);
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public R invoke(P parameter) {
        byte[] messageAsByte = GsonMarshaller.INSTANCE.serialize(parameter);
        if (queueDefinition.getReturnMessageClass().equals(RabbitMessage.None.class)) {
            sendOneWay(messageAsByte);
            return null;
        }
        byte[] resultAsByte = sendRPC(messageAsByte, 1000);
        return GsonMarshaller.INSTANCE.deserialize(queueDefinition.getReturnMessageClass(), resultAsByte);
    }

    /**
     * Send the message.
     *
     * @param messageAsByte the message as byte array
     */
    private void sendOneWay(byte[] messageAsByte) {
        try {
            createChannel().basicPublish("", queueDefinition.getName(), null, messageAsByte);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send the message and wait for the response.
     *
     * @param messageAsByte the message as byte array
     * @param timeout       the timeout in millis. If the timeout is reach, an TimeOutException will be throw
     * @return the response as byte array
     */
    private byte[] sendRPC(byte[] messageAsByte, long timeout) {
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
            channel.basicPublish("", queueDefinition.getName(), props, messageAsByte);

            long time = timeout - (System.currentTimeMillis() - startTimeMillis);
            while (time > 0) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    return delivery.getBody();
                }
                time = timeout - (System.currentTimeMillis() - startTimeMillis);
            }
            throw new RuntimeException("Time out ....");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    throw new RuntimeException(e);
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
        channel.queueDeclare(queueDefinition.getName(), false, false, false, null);
        return channel;
    }
}
