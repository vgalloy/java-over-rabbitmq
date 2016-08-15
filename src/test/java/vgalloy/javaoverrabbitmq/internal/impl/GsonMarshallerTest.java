package vgalloy.javaoverrabbitmq.internal.impl;

import com.google.gson.Gson;
import org.junit.Test;
import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;

import static org.junit.Assert.assertEquals;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class GsonMarshallerTest {

    private final GsonMarshaller gsonMarshaller = GsonMarshaller.INSTANCE;

    @Test
    public void serialization1() {
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
}