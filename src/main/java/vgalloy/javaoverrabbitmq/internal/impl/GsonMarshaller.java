package vgalloy.javaoverrabbitmq.internal.impl;

import com.google.gson.Gson;
import vgalloy.javaoverrabbitmq.api.RabbitMessageMarshaller;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class GsonMarshaller implements RabbitMessageMarshaller {

    public static final GsonMarshaller INSTANCE = new GsonMarshaller();

    /**
     * Constructor.
     * To prevent external instantiation.
     */
    private GsonMarshaller() {
    }

    @Override
    public <T> byte[] serialize(T message) {
        Gson gson = new Gson();
        String string = gson.toJson(message);
        return string.getBytes();
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Gson gson = new Gson();
        String messageAsString = new String(bytes);
        return gson.fromJson(messageAsString, clazz);
    }
}
