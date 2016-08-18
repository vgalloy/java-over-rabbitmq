package vgalloy.javaoverrabbitmq.internal.client;

import com.rabbitmq.client.Connection;
import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class ConsumerClientProxy<P> extends AbstractClient implements Consumer<P> {

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
            createChannel().basicPublish("", consumerQueueDefinition.getName(), null, messageAsByte);
        } catch (IOException e) {
            throw new JavaOverRabbitException(e);
        }
    }
}
