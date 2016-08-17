package vgalloy.javaoverrabbitmq.internal.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.marshaller.GsonMarshaller;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class ConsumerClientProxy<P> implements Consumer<P> {

    private final ConsumerQueueDefinition<P> functionQueueDefinition;
    private final Connection connection;

    /**
     * Constructor.
     *
     * @param connection              the connection
     * @param functionQueueDefinition the functionQueueDefinition
     */
    public ConsumerClientProxy(Connection connection, ConsumerQueueDefinition<P> functionQueueDefinition) {
        this.functionQueueDefinition = Objects.requireNonNull(functionQueueDefinition);
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void accept(P parameter) {
        byte[] messageAsByte = GsonMarshaller.INSTANCE.serialize(parameter);
        sendOneWay(messageAsByte);
    }

    /**
     * Send the message.
     *
     * @param messageAsByte the message as byte array
     */
    private void sendOneWay(byte... messageAsByte) {
        try {
            createChannel().basicPublish("", functionQueueDefinition.getName(), null, messageAsByte);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a new Channel for method invocation.
     *
     * @return the new chanel
     * @throws IOException if the channel can not be create
     */
    private Channel createChannel() throws IOException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(functionQueueDefinition.getName(), false, false, false, null);
        return channel;
    }
}
