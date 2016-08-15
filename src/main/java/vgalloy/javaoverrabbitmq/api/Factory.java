package vgalloy.javaoverrabbitmq.api;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueue;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;
import vgalloy.javaoverrabbitmq.internal.impl.ClientProxy;
import vgalloy.javaoverrabbitmq.internal.impl.RabbitConsumerImpl;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class Factory {

    /**
     * Create a proxy for remote invocation.
     *
     * @param connectionFactory the connection factory
     * @param rpcQueue          the RPCQueue
     * @param <P>               the parameter message
     * @param <R>               the result message
     * @return a proxy for remote call
     */
    public static <P extends RabbitMessage, R extends RabbitMessage> RPCQueueMethod<P, R> createClient(ConnectionFactory connectionFactory, RPCQueue<P, R> rpcQueue) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        RPCQueueMethod<P, R> proxy;
        try {
            proxy = new ClientProxy<>(connectionFactory.newConnection(), rpcQueue);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Can not get a connection", e);
        }
        return proxy;
    }

    /**
     * Create a consumer.
     *
     * @param connectionFactory the connection factory
     * @param rpcQueue          the RPCQueue to connect
     * @param implementation    the implementation
     * @param <P>               the parameter message
     * @param <R>               the result message
     * @return a rabbit consumer
     */
    public static <P extends RabbitMessage, R extends RabbitMessage> RabbitConsumer createConsumer(ConnectionFactory connectionFactory, RPCQueue<P, R> rpcQueue, RPCQueueMethod<P, R> implementation) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        Objects.requireNonNull(rpcQueue, "RPCQueue can not be null");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);
            channel.queueDeclare(rpcQueue.getName(), false, false, false, null);
            RabbitConsumerImpl<P, R> rabbitConsumer = new RabbitConsumerImpl<>(channel, rpcQueue, implementation);
            channel.basicConsume(rpcQueue.getName(), false, rabbitConsumer);

            return rabbitConsumer;
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Can not get a connection", e);
        }
    }
}

