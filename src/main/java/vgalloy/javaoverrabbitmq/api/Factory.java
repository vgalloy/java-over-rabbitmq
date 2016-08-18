package vgalloy.javaoverrabbitmq.api;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.client.ConsumerClientProxy;
import vgalloy.javaoverrabbitmq.internal.client.FunctionClientProxy;
import vgalloy.javaoverrabbitmq.internal.consumer.ConsumerRabbitConsumerImpl;
import vgalloy.javaoverrabbitmq.internal.consumer.FunctionRabbitConsumerImpl;
import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.internal.queue.ConsumerQueueDefinitionImpl;
import vgalloy.javaoverrabbitmq.internal.queue.FunctionQueueDefinitionImpl;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

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
     * @param connectionFactory       the connection factory
     * @param functionQueueDefinition the FunctionQueueDefinitionImpl
     * @param <P>                     the parameter message
     * @param <R>                     the result message
     * @return a proxy for remote call
     */
    public static <P, R> Function<P, R> createClient(ConnectionFactory connectionFactory, FunctionQueueDefinition<P, R> functionQueueDefinition) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        try {
            return new FunctionClientProxy<>(functionQueueDefinition, connectionFactory.newConnection());
        } catch (IOException | TimeoutException e) {
            throw new JavaOverRabbitException("Can not get a connection", e);
        }
    }

    /**
     * Create a proxy for remote invocation.
     *
     * @param connectionFactory       the connection factory
     * @param consumerQueueDefinition the consumerQueueDefinition
     * @param <P>                     the parameter message
     * @return a proxy for remote call
     */
    public static <P> Consumer<P> createClient(ConnectionFactory connectionFactory, ConsumerQueueDefinition<P> consumerQueueDefinition) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        try {
            return new ConsumerClientProxy<>(consumerQueueDefinition, connectionFactory.newConnection());
        } catch (IOException | TimeoutException e) {
            throw new JavaOverRabbitException("Can not get a connection", e);
        }
    }

    /**
     * Create a consumer.
     *
     * @param connectionFactory       the connection factory
     * @param functionQueueDefinition the FunctionQueueDefinitionImpl to connect
     * @param implementation          the implementation
     * @param <P>                     the parameter message
     * @param <R>                     the result message
     * @return a rabbit consumer
     */
    public static <P, R> RabbitConsumer createConsumer(ConnectionFactory connectionFactory, FunctionQueueDefinition<P, R> functionQueueDefinition, Function<P, R> implementation) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        Objects.requireNonNull(functionQueueDefinition, "FunctionQueueDefinitionImpl can not be null");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(functionQueueDefinition.getName(), false, false, false, null);
            channel.basicQos(1);
            return new FunctionRabbitConsumerImpl<>(channel, functionQueueDefinition, implementation);
        } catch (IOException | TimeoutException e) {
            throw new JavaOverRabbitException("Can not get a connection", e);
        }
    }

    /**
     * Create a consumer.
     *
     * @param connectionFactory       the connection factory
     * @param consumerQueueDefinition the consumerQueueDefinition to connect
     * @param implementation          the implementation
     * @param <P>                     the parameter message
     * @return a rabbit consumer
     */
    public static <P> RabbitConsumer createConsumer(ConnectionFactory connectionFactory, ConsumerQueueDefinition<P> consumerQueueDefinition, Consumer<P> implementation) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        Objects.requireNonNull(consumerQueueDefinition, "consumerQueueDefinition can not be null");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(consumerQueueDefinition.getName(), false, false, false, null);
            channel.basicQos(1);
            channel.queueDeclare(consumerQueueDefinition.getName(), false, false, false, null);
            return new ConsumerRabbitConsumerImpl<>(channel, consumerQueueDefinition, implementation);
        } catch (IOException | TimeoutException e) {
            throw new JavaOverRabbitException("Can not get a connection", e);
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
    public static <P, R> FunctionQueueDefinition<P, R> createQueue(String name, Class<P> parameterMessageClass, Class<R> returnMessageClass) {
        return new FunctionQueueDefinitionImpl<>(name, parameterMessageClass, returnMessageClass);
    }

    /**
     * Create a queue.
     *
     * @param name                  the queue name.
     * @param parameterMessageClass the Java class parameter message representation
     * @param <P>                   the parameter type
     * @return the queue definition
     */
    public static <P> ConsumerQueueDefinition<P> createQueue(String name, Class<P> parameterMessageClass) {
        return new ConsumerQueueDefinitionImpl<>(name, parameterMessageClass);
    }
}

