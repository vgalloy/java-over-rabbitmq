package com.vgalloy.javaoverrabbitmq.api.marshaller.impl;

import com.google.gson.Gson;
import org.junit.Test;

import com.vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;
import com.vgalloy.javaoverrabbitmq.utils.fake.marshaller.GsonMarshaller;
import com.vgalloy.javaoverrabbitmq.utils.fake.message.DoubleIntegerMessage;
import com.vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
 */
public final class GsonMarshallerTest {

    private final RabbitMessageMarshaller gsonMarshaller = GsonMarshaller.INSTANCE;

    @Test
    public void testSimpleSerialization() {
        DoubleIntegerMessage doubleIntegerMessage = new DoubleIntegerMessage(1, 2);
        assertEquals(doubleIntegerMessage, gsonMarshaller.deserialize(DoubleIntegerMessage.class, gsonMarshaller.serialize(doubleIntegerMessage)));
    }

    @Test
    public void serializationWithGson() {
        DoubleIntegerMessage doubleIntegerMessage = new DoubleIntegerMessage(1, 2);
        Gson gson = new Gson();
        assertEquals(doubleIntegerMessage, gson.fromJson(gson.toJson(doubleIntegerMessage), DoubleIntegerMessage.class));
    }

    @Test
    public void serializationByByteArray() {
        String message = "Ok Bro";
        assertEquals(message, new String(message.getBytes()));
    }

    @Test
    public void testNullSerialization() {
        IntegerMessage result = gsonMarshaller.deserialize(IntegerMessage.class, gsonMarshaller.serialize(null));
        assertNull(result);
    }
}