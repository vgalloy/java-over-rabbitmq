package com.vgalloy.javaoverrabbitmq.utils.fake.marshaller;

import com.google.gson.Gson;

import com.vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
 */
public enum GsonMarshaller implements RabbitMessageMarshaller {
    INSTANCE;

    @Override
    public byte[] serialize(Object message) {
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
