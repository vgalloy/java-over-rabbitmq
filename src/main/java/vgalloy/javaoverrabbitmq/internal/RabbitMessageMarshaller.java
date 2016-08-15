package vgalloy.javaoverrabbitmq.internal;

import vgalloy.javaoverrabbitmq.api.RabbitMessage;
import vgalloy.javaoverrabbitmq.api.exception.DeserializationException;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 *         // TODO unused Class
 */
public interface RabbitMessageMarshaller<M extends RabbitMessage> {

    /**
     * Serialize the object into a byte array.
     *
     * @param message the message
     * @return the message as a byte array
     */
    byte[] serialize(M message);

    /**
     * Deserialize the message.
     *
     * @param bytes the message as a byte array
     * @return the message as a Java Object
     * @throws DeserializationException An exception can be throw if the message can not be instanciate
     */
    M deserialize(byte[] bytes) throws DeserializationException;
}
