package vgalloy.javaoverrabbitmq.internal.marshaller.impl;

import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;
import vgalloy.javaoverrabbitmq.internal.marshaller.RabbitMessageMarshaller;
import vgalloy.javaoverrabbitmq.internal.message.RabbitResponse;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class GlobalMarshaller implements RabbitMessageMarshaller {

    public static final RabbitMessageMarshaller INSTANCE = new GlobalMarshaller();

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
        byte[] responseAsByte = rabbitMessageMarshaller.serialize(message);
        boolean isError = message != null && RabbitConsumerException.class.isAssignableFrom(message.getClass());
        RabbitResponse rabbitResponse = new RabbitResponse(responseAsByte, isError);
        return rabbitMessageMarshaller.serialize(rabbitResponse);
    }

    @Override
    public <M> M deserialize(Class<M> clazz, byte... bytes) {
        RabbitResponse rabbitResponse = rabbitMessageMarshaller.deserialize(RabbitResponse.class, bytes);
        if (rabbitResponse.isError()) {
            RabbitConsumerException rabbitConsumerException = rabbitMessageMarshaller.deserialize(RabbitConsumerException.class, rabbitResponse.getObjectAsByteArray());
            throw new RabbitRemoteException(rabbitConsumerException);
        }
        return rabbitMessageMarshaller.deserialize(clazz, rabbitResponse.getObjectAsByteArray());
    }
}
