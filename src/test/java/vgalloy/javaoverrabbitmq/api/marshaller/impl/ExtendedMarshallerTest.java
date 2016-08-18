package vgalloy.javaoverrabbitmq.api.marshaller.impl;

import org.junit.Test;
import vgalloy.javaoverrabbitmq.api.fake.marshaller.GsonMarshaller;
import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;
import vgalloy.javaoverrabbitmq.internal.marshaller.impl.ExtendedMarshaller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class ExtendedMarshallerTest {

    private final RabbitMessageMarshaller marshaller = GsonMarshaller.INSTANCE;

    @Test
    public void testSimpleSerialization() {
        DoubleIntegerMessage doubleIntegerMessage = new DoubleIntegerMessage(2, 3);
        assertEquals(doubleIntegerMessage, ExtendedMarshaller.deserialize(marshaller, DoubleIntegerMessage.class, false, marshaller.serialize(doubleIntegerMessage)));
    }

    @Test
    public void testErrorSerialization() {
        RabbitConsumerException rabbitConsumerException = new RabbitConsumerException("An Exception");
        try {
            ExtendedMarshaller.deserialize(marshaller, DoubleIntegerMessage.class, true, marshaller.serialize(rabbitConsumerException));
            fail("No Exception ! ! ");
        } catch (RabbitRemoteException e) {
            assertEquals(rabbitConsumerException.getMessage(), e.getMessage());
        }
    }

    @Test
    public void testNullSerialization() {
        DoubleIntegerMessage result =  ExtendedMarshaller.deserialize(marshaller, DoubleIntegerMessage.class, false, marshaller.serialize(null));
        assertNull(result);
    }
}