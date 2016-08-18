package vgalloy.javaoverrabbitmq.api.marshaller.impl;

import org.junit.Test;
import vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;
import vgalloy.javaoverrabbitmq.utils.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class DefaultMarshallerTest {

    private final RabbitMessageMarshaller marshaller = DefaultMarshaller.INSTANCE;

    @Test
    public void testSimpleSerialization() {
        DoubleIntegerMessage doubleIntegerMessage = new DoubleIntegerMessage(1, 2);
        assertEquals(doubleIntegerMessage, marshaller.deserialize(DoubleIntegerMessage.class, marshaller.serialize(doubleIntegerMessage)));
    }

    @Test
    public void testNullSerialization() {
        IntegerMessage result = marshaller.deserialize(IntegerMessage.class, marshaller.serialize(null));
        assertNull(result);
    }
}