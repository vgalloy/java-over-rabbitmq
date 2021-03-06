package com.vgalloy.javaoverrabbitmq.internal.client;

import java.io.IOException;
import java.util.Objects;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import com.vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import com.vgalloy.javaoverrabbitmq.api.model.RabbitClientConsumer;
import com.vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
 */
public final class ConsumerClientProxy<P> extends AbstractClient implements RabbitClientConsumer<P> {

    private final ConsumerQueueDefinition<P> consumerQueueDefinition;

    /**
     * Constructor.
     *
     * @param consumerQueueDefinition the queue definition
     * @param connection              the connection
     */
    public ConsumerClientProxy(ConsumerQueueDefinition<P> consumerQueueDefinition, Connection connection) {
        super(consumerQueueDefinition, connection);
        this.consumerQueueDefinition = Objects.requireNonNull(consumerQueueDefinition);
    }

    @Override
    public void accept(P parameter) {
        byte[] messageAsByte = consumerQueueDefinition.getMarshaller().serialize(parameter);
        sendOneWay(messageAsByte);
    }

    /**
     * Send the message.
     *
     * @param messageAsByte the message as byte array
     */
    private void sendOneWay(byte... messageAsByte) {
        try {
            Channel channel = getChannel();
            channel.basicPublish("", consumerQueueDefinition.getName(), null, messageAsByte);
        } catch (IOException e) {
            throw new JavaOverRabbitException(e);
        }
    }
}
