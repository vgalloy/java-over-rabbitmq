package vgalloy.javaoverrabbitmq.api;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public interface RabbitMessageMarshaller {

    /**
     * Serialize a message into a byte array.
     *
     * @param message the message as an object
     * @param <M>     the type of the message
     * @return the message as a byte array
     */
    <M> byte[] serialize(M message);

    /**
     * Deserialize the message.
     *
     * @param clazz The Java object representation of the message
     * @param bytes the message as a byte array
     * @param <M>   the type of the message
     * @return the message as an object
     */
    <M> M deserialize(Class<M> clazz, byte[] bytes);
}
