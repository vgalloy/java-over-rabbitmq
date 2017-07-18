package com.vgalloy.javaoverrabbitmq.internal.marshaller;

import com.vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;

/**
 * Created by Vincent Galloy on 10/06/17.
 * Based on a {@link com.vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller}. This Marshaller deserialize message and exception.
 *
 * @author Vincent Galloy
 */
public interface ExtendedMarshaller {

    /**
     * Deserialize with Error.
     *
     * @param clazz the class
     * @param bytes the message as byte array
     * @param <M>   the message type
     * @return the message
     */
    <M> M deserialize(Class<M> clazz, byte... bytes);

    /**
     * Deserialize with Error.
     *
     * @param bytes the message as byte array
     * @return the message
     */
    RabbitRemoteException deserializeError(byte... bytes);
}
