package com.vgalloy.javaoverrabbitmq.api.factory;

import java.util.function.Consumer;
import java.util.function.Function;

import com.rabbitmq.client.ConnectionFactory;

import com.vgalloy.javaoverrabbitmq.api.model.RabbitClientConsumer;
import com.vgalloy.javaoverrabbitmq.api.model.RabbitClientFunction;
import com.vgalloy.javaoverrabbitmq.api.model.RabbitElement;
import com.vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import com.vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
 */
public interface RabbitmqFactory {

    /**
     * Return a instance of {@link RabbitmqFactory}.
     *
     * @return a RabbitmqFactoryBuilder
     */
    static RabbitmqFactory getInstance() {
        return RabbitmqFactoryBuilderLoader.INSTANCE.load().build();
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
    <P, R> RabbitClientFunction<P, R> createClient(ConnectionFactory connectionFactory, FunctionQueueDefinition<P, R> functionQueueDefinition);

    /**
     * Create a proxy for remote invocation.
     *
     * @param connectionFactory       the connection factory
     * @param consumerQueueDefinition the consumerQueueDefinition
     * @param <P>                     the parameter message
     * @return a proxy for remote call
     */
    <P> RabbitClientConsumer<P> createClient(ConnectionFactory connectionFactory, ConsumerQueueDefinition<P> consumerQueueDefinition);

    /**
     * Create a consumer.
     *
     * @param connectionFactory       the connection factory
     * @param functionQueueDefinition the FunctionQueueDefinition to connect
     * @param implementation          the implementation
     * @param <P>                     the parameter message
     * @param <R>                     the result message
     * @return a rabbit consumer
     */
    <P, R> RabbitElement createConsumer(ConnectionFactory connectionFactory, FunctionQueueDefinition<P, R> functionQueueDefinition, Function<P, R> implementation);

    /**
     * Create a consumer.
     *
     * @param connectionFactory       the connection factory
     * @param consumerQueueDefinition the consumerQueueDefinition to connect
     * @param implementation          the implementation
     * @param <P>                     the parameter message
     * @return a rabbit consumer
     */
    <P> RabbitElement createConsumer(ConnectionFactory connectionFactory, ConsumerQueueDefinition<P> consumerQueueDefinition, Consumer<P> implementation);

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
    <P, R> FunctionQueueDefinition<P, R> createQueue(String name, Class<P> parameterMessageClass, Class<R> returnMessageClass);

    /**
     * Create a queue.
     *
     * @param name                  the queue name.
     * @param parameterMessageClass the Java class parameter message representation
     * @param <P>                   the parameter type
     * @return the queue definition
     */
    <P> ConsumerQueueDefinition<P> createQueue(String name, Class<P> parameterMessageClass);
}

