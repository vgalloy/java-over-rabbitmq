package vgalloy.javaoverrabbitmq.api.marshaller.impl;

import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class DefaultMarshaller implements RabbitMessageMarshaller {

    public static final RabbitMessageMarshaller INSTANCE = new DefaultMarshaller();

    /**
     * Constructor.
     *
     * To prevent external instantiation.
     */
    private DefaultMarshaller() {
    }

    @Override
    public <T> byte[] serialize(T message) {
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
            return  (T) ois.readObject();
        } catch (Exception e) {
            throw new JavaOverRabbitException(e);
        } finally {
            closeSilently(bis);
            closeSilently(ois);
        }
    }

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
}
