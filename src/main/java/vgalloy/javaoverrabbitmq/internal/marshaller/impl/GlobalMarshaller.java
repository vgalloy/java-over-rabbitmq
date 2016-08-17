package vgalloy.javaoverrabbitmq.internal.marshaller.impl;

import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;
import vgalloy.javaoverrabbitmq.internal.marshaller.RabbitMessageMarshaller;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class GlobalMarshaller implements RabbitMessageMarshaller {

    public static final GlobalMarshaller INSTANCE = new GlobalMarshaller();

    private final RabbitMessageMarshaller rabbitMessageMarshaller = GsonMarshaller.INSTANCE;

    /**
     * Constructor.
     * <p>
     * To prevent external instantiation.
     */
    private GlobalMarshaller() {
    }

    @Override
    public <M> byte[] serialize(M message) {
        return rabbitMessageMarshaller.serialize(message);
    }

    @Override
    public <M> M deserialize(Class<M> clazz, byte... bytes) {
        return rabbitMessageMarshaller.deserialize(clazz, bytes);
    }

    /**
     * Deserialize with Error.
     *
     * @param clazz   the class
     * @param isError if true, the bytes should represente a {@link RabbitConsumerException}
     * @param bytes   the message as byte array
     * @param <M>     the message type
     * @return the message
     */
    public <M> M deserialize(Class<M> clazz, boolean isError, byte... bytes) {
        if (isError) {
            RabbitConsumerException rabbitConsumerException = rabbitMessageMarshaller.deserialize(RabbitConsumerException.class, bytes);
            throw new RabbitRemoteException(rabbitConsumerException);
        }
        return deserialize(clazz, bytes);
    }
}
