package vgalloy.javaoverrabbitmq.internal.impl;

import com.google.gson.Gson;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class GsonMarshaller {

    public static final GsonMarshaller INSTANCE = new GsonMarshaller();

    /**
     * Constructor.
     * To prevent external instantiation.
     */
    private GsonMarshaller() {
    }

    /**
     * Serialize a message into a byte array.
     *
     * @param message the message as an object
     * @param <T>     the type of the message
     * @return the message as a byte array
     */
    public <T> byte[] serialize(T message) {
        Gson gson = new Gson();
        String string = gson.toJson(message);
        return string.getBytes();
    }

    /**
     * Deserialize the message.
     *
     * @param clazz The Java object representation of the message
     * @param bytes the message as a byte array
     * @param <T>   the type of the message
     * @return the message as an object
     */
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Gson gson = new Gson();
        String messageAsString = new String(bytes);
        return gson.fromJson(messageAsString, clazz);
    }
}
