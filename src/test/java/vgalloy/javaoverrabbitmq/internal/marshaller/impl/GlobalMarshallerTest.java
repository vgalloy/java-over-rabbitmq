package vgalloy.javaoverrabbitmq.internal.marshaller.impl;

import org.junit.Test;

import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;
import vgalloy.javaoverrabbitmq.internal.marshaller.RabbitMessageMarshaller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class GlobalMarshallerTest {

    private final RabbitMessageMarshaller gsonMarshaller = GlobalMarshaller.INSTANCE;

    @Test
    public void testSimpleSerialization() {
        DoubleIntegerMessage doubleIntegerMessage = new DoubleIntegerMessage(2, 3);
        assertEquals(doubleIntegerMessage, gsonMarshaller.deserialize(DoubleIntegerMessage.class, gsonMarshaller.serialize(doubleIntegerMessage)));
    }

    @Test
    public void testErrorSerialization() {
        RabbitConsumerException rabbitConsumerException = new RabbitConsumerException("An Exception");
        try {
            gsonMarshaller.deserialize(DoubleIntegerMessage.class, gsonMarshaller.serialize(rabbitConsumerException));
            fail("No Exception ! ! ");
        } catch (RabbitRemoteException e) {
            assertEquals(rabbitConsumerException.getMessage(), e.getMessage());
        }
    }

    @Test
    public void testNullSerialization() {
        DoubleIntegerMessage result = gsonMarshaller.deserialize(DoubleIntegerMessage.class, gsonMarshaller.serialize(null));
        assertNull(result);
    }
}