package vgalloy.javaoverrabbitmq.api.marshaller.impl;

import org.junit.Test;

import vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitConsumerException;
import vgalloy.javaoverrabbitmq.internal.exception.RabbitRemoteException;
import vgalloy.javaoverrabbitmq.internal.marshaller.impl.ExtendedMarshaller;
import vgalloy.javaoverrabbitmq.utils.fake.marshaller.GsonMarshaller;
import vgalloy.javaoverrabbitmq.utils.fake.message.DoubleIntegerMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class ExtendedMarshallerTest {

    private final RabbitMessageMarshaller marshaller = GsonMarshaller.INSTANCE;

    @Test
    public void testSimpleSerialization() {
        // GIVEN
        DoubleIntegerMessage doubleIntegerMessage = new DoubleIntegerMessage(2, 3);

        // WHEN
        DoubleIntegerMessage result = ExtendedMarshaller.deserialize(marshaller, DoubleIntegerMessage.class, marshaller.serialize(doubleIntegerMessage));

        // THEN
        assertEquals(doubleIntegerMessage, result);
    }

    @Test
    public void testErrorSerialization() {
        // GIVEN
        RabbitConsumerException rabbitConsumerException = new RabbitConsumerException("An Exception");

        // WHEN
        RabbitRemoteException exception = ExtendedMarshaller.deserializeError(marshaller, marshaller.serialize(rabbitConsumerException));

        // THEN
        assertEquals(rabbitConsumerException.getMessage(), exception.getMessage());
    }

    @Test
    public void testNullSerialization() {
        // GIVEN
        DoubleIntegerMessage message = null;

        // WHEN
        DoubleIntegerMessage result = ExtendedMarshaller.deserialize(marshaller, DoubleIntegerMessage.class, marshaller.serialize(message));

        // THEN
        assertNull(result);
    }
}