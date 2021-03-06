package com.vgalloy.javaoverrabbitmq.api.marshaller.impl;

import org.junit.Test;

import com.vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;
import com.vgalloy.javaoverrabbitmq.utils.fake.message.DoubleIntegerMessage;
import com.vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
 */
public final class DefaultMarshallerTest {

    private final RabbitMessageMarshaller marshaller = DefaultMarshaller.INSTANCE;

    @Test
    public void testSimpleSerialization() {
        // GIVEN
        DoubleIntegerMessage doubleIntegerMessage = new DoubleIntegerMessage(1, 2);

        // WHEN
        DoubleIntegerMessage result = marshaller.deserialize(DoubleIntegerMessage.class, marshaller.serialize(doubleIntegerMessage));
        // THEN
        assertEquals(doubleIntegerMessage, result);
    }

    @Test
    public void testNullSerialization() {
        // GIVEN
        IntegerMessage message = null;

        // WHEN
        IntegerMessage result = marshaller.deserialize(IntegerMessage.class, marshaller.serialize(message));

        // THEN
        assertNull(result);
    }
}