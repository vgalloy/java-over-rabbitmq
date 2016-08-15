package vgalloy.javaoverrabbitmq.internal.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.RabbitMessage;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueue;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class RabbitConsumerImpl<P extends RabbitMessage, R extends RabbitMessage> extends QueueingConsumer implements RabbitConsumer {

    private final RPCQueue<P, R> rpcQueue;
    private final RPCQueueMethod<P, R> service;

    /**
     * Constructor.
     *
     * @param channel  the channel
     * @param rpcQueue the rpcQueue
     * @param service  the service implementation
     */
    public RabbitConsumerImpl(Channel channel, RPCQueue<P, R> rpcQueue, RPCQueueMethod<P, R> service) {
        super(channel);
        this.rpcQueue = Objects.requireNonNull(rpcQueue);
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        P paramAsObject = GsonMarshaller.INSTANCE.deserialize(rpcQueue.getParameterMessageClass(), body);
        R result = service.invoke(paramAsObject);

        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                .Builder()
                .correlationId(properties.getCorrelationId())
                .build();

        byte[] resultAsByte = GsonMarshaller.INSTANCE.serialize(result);

        getChannel().basicPublish("", properties.getReplyTo(), replyProps, resultAsByte);
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }

    @Override
    public void close() {
        try {
            getChannel().close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
