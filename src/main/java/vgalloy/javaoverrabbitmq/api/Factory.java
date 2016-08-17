package vgalloy.javaoverrabbitmq.api;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import vgalloy.javaoverrabbitmq.api.message.RabbitMessage;
import vgalloy.javaoverrabbitmq.api.queue.QueueDefinition;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;
import vgalloy.javaoverrabbitmq.internal.consumer.RPCRabbitConsumerImpl;
import vgalloy.javaoverrabbitmq.internal.consumer.SimpleRabbitConsumerImpl;
import vgalloy.javaoverrabbitmq.internal.impl.ClientProxy;
import vgalloy.javaoverrabbitmq.internal.queue.RPCQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.queue.SimpleQueueDefinition;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class Factory {

    /**
     * Constructor.
     * To prevent instantiation
     */
    private Factory() {
        throw new AssertionError();
    }

    /**
     * Create a proxy for remote invocation.
     *
     * @param connectionFactory the connection factory
     * @param queueDefinition   the QueueDefinition
     * @param <P>               the parameter message
     * @param <R>               the result message
     * @return a proxy for remote call
     */
    // TODO Replace Consumer && function
    public static <P extends RabbitMessage, R extends RabbitMessage> RPCQueueMethod<P, R> createClient(ConnectionFactory connectionFactory, QueueDefinition<P, R> queueDefinition) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        RPCQueueMethod<P, R> proxy;
        try {
            proxy = new ClientProxy<>(connectionFactory.newConnection(), queueDefinition);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Can not get a connection", e);
        }
        return proxy;
    }

    /**
     * Create a consumer.
     *
     * @param connectionFactory the connection factory
     * @param queueDefinition   the QueueDefinition to connect
     * @param implementation    the implementation
     * @param <P>               the parameter message
     * @param <R>               the result message
     * @return a rabbit consumer
     */
    public static <P extends RabbitMessage, R extends RabbitMessage> RabbitConsumer createConsumer(ConnectionFactory connectionFactory, QueueDefinition<P, R> queueDefinition, RPCQueueMethod<P, R> implementation) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        Objects.requireNonNull(queueDefinition, "QueueDefinition can not be null");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);
            channel.queueDeclare(queueDefinition.getName(), false, false, false, null);
            if (queueDefinition.getReturnMessageClass().equals(RabbitMessage.None.class)) {
                SimpleRabbitConsumerImpl<P> rabbitConsumer = new SimpleRabbitConsumerImpl<>(channel, (QueueDefinition<P, RabbitMessage.None>) queueDefinition, (RPCQueueMethod<P, RabbitMessage.None>) implementation);
                channel.basicConsume(queueDefinition.getName(), false, rabbitConsumer);
                return rabbitConsumer;
            } else {
                RPCRabbitConsumerImpl<P, R> rabbitConsumer = new RPCRabbitConsumerImpl<>(channel, queueDefinition, implementation);
                channel.basicConsume(queueDefinition.getName(), false, rabbitConsumer);
                return rabbitConsumer;
            }
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Can not get a connection", e);
        }
    }

    /**
     * Create a queue.
     *
     * @param name                  the queue name.
     * @param parameterMessageClass the Java class parameter message representation
     * @param returnMessageClass    the Java class return message representation
     * @param <P>                   the parameter type
     * @param <R>                   the return type
     * @return the queue definition
     */
    public static <P extends RabbitMessage, R extends RabbitMessage> QueueDefinition<P, R> createQueue(String name, Class<P> parameterMessageClass, Class<R> returnMessageClass) {
        return new RPCQueueDefinition<>(name, parameterMessageClass, returnMessageClass);
    }

    /**
     * Create a queue.
     *
     * @param name                  the queue name.
     * @param parameterMessageClass the Java class parameter message representation
     * @param <P>                   the parameter type
     * @return the queue definition
     */
    public static <P extends RabbitMessage> QueueDefinition<P, RabbitMessage.None> createQueue(String name, Class<P> parameterMessageClass) {
        return new SimpleQueueDefinition<>(name, parameterMessageClass);
    }
}

