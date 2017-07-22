package com.vgalloy.javaoverrabbitmq.api.marshaller.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import com.vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
 */
public enum  DefaultMarshaller implements RabbitMessageMarshaller {
    INSTANCE;

    /**
     * Close the closable. Throw a {@link JavaOverRabbitException} if error occurred.
     *
     * @param closeable the closable, can be null
     */
    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new JavaOverRabbitException(e);
            }
        }
    }

    @Override
    public byte[] serialize(Object message) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream oos = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(byteArrayOutputStream);
            oos.writeObject(message);
            oos.flush();
            oos.reset();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new JavaOverRabbitException(e);
        } finally {
            closeSilently(byteArrayOutputStream);
            closeSilently(oos);
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            return clazz.cast(ois.readObject());
        } catch (Exception e) {
            throw new JavaOverRabbitException(e);
        } finally {
            closeSilently(bis);
            closeSilently(ois);
        }
    }
}
