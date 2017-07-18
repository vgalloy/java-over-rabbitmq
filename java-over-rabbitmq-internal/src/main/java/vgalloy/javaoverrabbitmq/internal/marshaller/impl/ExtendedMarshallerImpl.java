package vgalloy.javaoverrabbitmq.internal.marshaller.impl;

import java.util.Objects;

import vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;
import vgalloy.javaoverrabbitmq.internal.marshaller.ExtendedMarshaller;

/**
 * Created by Vincent Galloy on 17/08/16.
 * This Marshaller used a {@link RabbitMessageMarshaller} to parse message and exception.
 *
 * @author Vincent Galloy
 */
public final class ExtendedMarshallerImpl implements ExtendedMarshaller {

    private final RabbitMessageMarshaller rabbitMessageMarshaller;

    /**
     * Constructor.
     *
     * @param rabbitMessageMarshaller the marshaller used to parse message and exception
     */
    public ExtendedMarshallerImpl(RabbitMessageMarshaller rabbitMessageMarshaller) {
        this.rabbitMessageMarshaller = Objects.requireNonNull(rabbitMessageMarshaller);
    }

    @Override
    public <M> M deserialize(Class<M> clazz, byte... bytes) {
        return rabbitMessageMarshaller.deserialize(clazz, bytes);
    }

    @Override
    public RabbitRemoteException deserializeError(byte... bytes) {
        RabbitConsumerException rabbitConsumerException = rabbitMessageMarshaller.deserialize(RabbitConsumerException.class, bytes);
        return new RabbitRemoteException(rabbitConsumerException);
    }
}
