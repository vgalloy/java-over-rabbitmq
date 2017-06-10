package vgalloy.javaoverrabbitmq.internal.client;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.api.model.RabbitClientFunction;
import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;
import vgalloy.javaoverrabbitmq.internal.marshaller.ExtendedMarshaller;
import vgalloy.javaoverrabbitmq.internal.marshaller.impl.ExtendedMarshallerImpl;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
 */
public final class FunctionClientProxy<P, R> extends AbstractClient implements RabbitClientFunction<P, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionClientProxy.class);

    private final FunctionQueueDefinition<P, R> functionQueueDefinition;
    private final ExtendedMarshaller extendedMarshaller;

    /**
     * Constructor.
     *
     * @param functionQueueDefinition the queue definition
     * @param connection              the connection
     */
    public FunctionClientProxy(FunctionQueueDefinition<P, R> functionQueueDefinition, Connection connection) {
        super(functionQueueDefinition, connection);
        this.functionQueueDefinition = Objects.requireNonNull(functionQueueDefinition);
        this.extendedMarshaller = new ExtendedMarshallerImpl(functionQueueDefinition.getMarshaller());
    }

    @Override
    public R apply(P parameter) {
        byte[] messageAsByte = functionQueueDefinition.getMarshaller().serialize(parameter);
        return sendRPC(messageAsByte);
    }

    /**
     * Send the message and wait for the response.
     *
     * @param messageAsByte the message as byte array
     * @return the response as byte array
     */
    private R sendRPC(byte[] messageAsByte) {
        Channel channel = null;

        try {
            channel = createChannel();
            String replyQueueName = channel.queueDeclare().getQueue();

            String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

            BlockingQueue<Supplier<R>> response = new ArrayBlockingQueue<>(1);

            channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    if (properties.getCorrelationId().equals(corrId)) {
                        boolean isError = properties.getHeaders() != null && properties.getHeaders().containsKey(RabbitConsumerException.ERROR_HEADER);
                        if (isError) {
                            RabbitRemoteException exception = extendedMarshaller.deserializeError(body);
                            response.offer(() -> {
                                throw exception;
                            });
                        } else {
                            R r = extendedMarshaller.deserialize(functionQueueDefinition.getReturnMessageClass(), body);
                            response.offer(() -> r);
                        }
                    }
                }
            });

            LOGGER.debug("send {}", messageAsByte);
            channel.basicPublish("", functionQueueDefinition.getName(), props, messageAsByte);
            return response.poll(functionQueueDefinition.getTimeoutMillis(), TimeUnit.MILLISECONDS).get();
        } catch (Exception e) {
            throw new JavaOverRabbitException(e);
        } finally {
            close(channel);
        }
    }
}
