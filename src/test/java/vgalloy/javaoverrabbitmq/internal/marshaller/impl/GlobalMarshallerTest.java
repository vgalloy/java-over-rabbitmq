package vgalloy.javaoverrabbitmq.internal.marshaller.impl;

import org.junit.Test;
import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class GlobalMarshallerTest {

    private final GlobalMarshaller marshaller = GlobalMarshaller.INSTANCE;

    @Test
    public void testSimpleSerialization() {
        DoubleIntegerMessage doubleIntegerMessage = new DoubleIntegerMessage(2, 3);
        assertEquals(doubleIntegerMessage, marshaller.deserialize(DoubleIntegerMessage.class, marshaller.serialize(doubleIntegerMessage)));
    }

    @Test
    public void testErrorSerialization() {
        RabbitConsumerException rabbitConsumerException = new RabbitConsumerException("An Exception");
        try {
            marshaller.deserialize(DoubleIntegerMessage.class, true, marshaller.serialize(rabbitConsumerException));
            fail("No Exception ! ! ");
        } catch (RabbitRemoteException e) {
            assertEquals(rabbitConsumerException.getMessage(), e.getMessage());
        }
    }

    @Test
    public void testNullSerialization() {
        DoubleIntegerMessage result = marshaller.deserialize(DoubleIntegerMessage.class, marshaller.serialize(null));
        assertNull(result);
    }
}