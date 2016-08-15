package vgalloy.javaoverrabbitmq.internal.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import vgalloy.javaoverrabbitmq.api.RabbitMessage;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueue;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class ClientProxy<P extends RabbitMessage, R extends RabbitMessage> implements RPCQueueMethod<P, R> {

    private final Connection connection;
    private final RPCQueue<P, R> rpcQueue;

    /**
     * Constructor.
     *
     * @param connection the connection
     * @param rpcQueue   the rpcQueue
     */
    public ClientProxy(Connection connection, RPCQueue<P, R> rpcQueue) {
        this.connection = Objects.requireNonNull(connection);
        this.rpcQueue = Objects.requireNonNull(rpcQueue);
    }

    @Override
    public R invoke(P parameter) {
        byte[] objectAsByte = GsonMarshaller.INSTANCE.serialize(parameter);
        byte[] resultAsByte = send(objectAsByte);
        return GsonMarshaller.INSTANCE.deserialize(rpcQueue.getReturnMessageClass(), resultAsByte);
    }

    /**
     * Send the message and wait for the response.
     *
     * @param messageAsByte the message as byte array
     * @return the response as byte array
     */
    private byte[] send(byte[] messageAsByte) {
        Channel channel = null;
        try {
            channel = getChannel();
            QueueingConsumer consumer = new QueueingConsumer(channel);
            String replyQueueName = channel.queueDeclare().getQueue();
            channel.basicConsume(replyQueueName, true, consumer);

            String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            channel.basicPublish("", rpcQueue.getName(), props, messageAsByte);

            long time = System.currentTimeMillis();

            while (true) { // TODO infinit wait
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    return delivery.getBody();
                }
                if (System.currentTimeMillis() - time > 1000) {
                    throw new RuntimeException("Time out ... ");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
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
    private Channel getChannel() throws IOException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(rpcQueue.getName(), false, false, false, null);
        return channel;
    }
}
