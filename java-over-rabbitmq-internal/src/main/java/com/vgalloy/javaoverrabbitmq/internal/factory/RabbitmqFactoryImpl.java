package com.vgalloy.javaoverrabbitmq.internal.factory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import com.vgalloy.javaoverrabbitmq.api.factory.RabbitmqFactory;
import com.vgalloy.javaoverrabbitmq.api.model.RabbitClientConsumer;
import com.vgalloy.javaoverrabbitmq.api.model.RabbitClientFunction;
import com.vgalloy.javaoverrabbitmq.api.model.RabbitElement;
import com.vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import com.vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import com.vgalloy.javaoverrabbitmq.internal.client.ConsumerClientProxy;
import com.vgalloy.javaoverrabbitmq.internal.client.FunctionClientProxy;
import com.vgalloy.javaoverrabbitmq.internal.consumer.ConsumerRabbitConsumerImpl;
import com.vgalloy.javaoverrabbitmq.internal.consumer.FunctionRabbitConsumerImpl;
import com.vgalloy.javaoverrabbitmq.internal.queue.impl.ConsumerQueueDefinitionImpl;
import com.vgalloy.javaoverrabbitmq.internal.queue.impl.FunctionQueueDefinitionImpl;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
 */
public final class RabbitmqFactoryImpl implements RabbitmqFactory {

    @Override
    public <P, R> RabbitClientFunction<P, R> createClient(ConnectionFactory connectionFactory, FunctionQueueDefinition<P, R> functionQueueDefinition) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        try {
            return new FunctionClientProxy<>(functionQueueDefinition, connectionFactory.newConnection());
        } catch (IOException | TimeoutException e) {
            throw new JavaOverRabbitException("Can not get a connection", e);
        }
    }

    @Override
    public <P> RabbitClientConsumer<P> createClient(ConnectionFactory connectionFactory, ConsumerQueueDefinition<P> consumerQueueDefinition) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        try {
            return new ConsumerClientProxy<>(consumerQueueDefinition, connectionFactory.newConnection());
        } catch (IOException | TimeoutException e) {
            throw new JavaOverRabbitException("Can not get a connection", e);
        }
    }

    @Override
    public <P, R> RabbitElement createConsumer(ConnectionFactory connectionFactory, FunctionQueueDefinition<P, R> functionQueueDefinition, Function<P, R> implementation) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        Objects.requireNonNull(functionQueueDefinition, "FunctionQueueDefinition can not be null");
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

    @Override
    public <P> RabbitElement createConsumer(ConnectionFactory connectionFactory, ConsumerQueueDefinition<P> consumerQueueDefinition, Consumer<P> implementation) {
        Objects.requireNonNull(connectionFactory, "ConnectionFactory can not be null");
        Objects.requireNonNull(consumerQueueDefinition, "consumerQueueDefinition can not be null");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(consumerQueueDefinition.getName(), false, false, false, null);
            channel.basicQos(1);
            return new ConsumerRabbitConsumerImpl<>(channel, consumerQueueDefinition, implementation);
        } catch (IOException | TimeoutException e) {
            throw new JavaOverRabbitException("Can not get a connection", e);
        }
    }

    @Override
    public <P, R> FunctionQueueDefinition<P, R> createQueue(String name, Class<P> parameterMessageClass, Class<R> returnMessageClass) {
        return new FunctionQueueDefinitionImpl<>(name, parameterMessageClass, returnMessageClass);
    }

    @Override
    public <P> ConsumerQueueDefinition<P> createQueue(String name, Class<P> parameterMessageClass) {
        return new ConsumerQueueDefinitionImpl<>(name, parameterMessageClass);
    }
}

