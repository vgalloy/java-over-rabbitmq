package vgalloy.javaoverrabbitmq.internal.marshaller.impl;

import vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class ExtendedMarshaller {

    /**
     * Constructor.
     * <p>
     * To prevent instantiation
     */
    private ExtendedMarshaller() {
        throw new AssertionError();
    }

    /**
     * Deserialize with Error.
     *
     * @param rabbitMessageMarshaller the marshaller
     * @param clazz                   the class
     * @param bytes                   the message as byte array
     * @param <M>                     the message type
     * @return the message
     */
    public static <M> M deserialize(RabbitMessageMarshaller rabbitMessageMarshaller, Class<M> clazz, byte... bytes) {
        return rabbitMessageMarshaller.deserialize(clazz, bytes);
    }

    /**
     * Deserialize with Error.
     *
     * @param rabbitMessageMarshaller the marshaller
     * @param bytes                   the message as byte array
     * @return the message
     */
    public static RabbitRemoteException deserializeError(RabbitMessageMarshaller rabbitMessageMarshaller, byte... bytes) {
        RabbitConsumerException rabbitConsumerException = rabbitMessageMarshaller.deserialize(RabbitConsumerException.class, bytes);
        return new RabbitRemoteException(rabbitConsumerException);
    }
}
