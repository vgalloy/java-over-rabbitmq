package vgalloy.javaoverrabbitmq.internal.client;

import java.util.function.Consumer;

import org.junit.Test;

import vgalloy.javaoverrabbitmq.api.Factory;
import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import vgalloy.javaoverrabbitmq.utils.fake.marshaller.GsonMarshaller;
import vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.utils.fake.method.AcceptNullMethodImpl;
import vgalloy.javaoverrabbitmq.utils.fake.method.SimpleConsumerImpl;
import vgalloy.javaoverrabbitmq.utils.util.BrokerUtils;
import vgalloy.javaoverrabbitmq.utils.util.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class ConsumerClientProxyITest {

    @Test
    public void testSimple() throws Exception {
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        queueDefinition.setMarshaller(GsonMarshaller.INSTANCE);
        SimpleConsumerImpl simpleQueueMethod = new SimpleConsumerImpl();
        RabbitConsumer rabbitConsumer2 = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, simpleQueueMethod);

        Consumer<IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);
        remote.accept(new IntegerMessage(1));

        synchronized (simpleQueueMethod) {
            if (simpleQueueMethod.getValue() == 1) {
                assertEquals(1, simpleQueueMethod.getValue());
            } else {
                simpleQueueMethod.wait(500);
            }
        }
        assertEquals(1, simpleQueueMethod.getValue());

        rabbitConsumer2.close();
    }

    @Test
    public void testNullAsArgument() throws Exception {
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        AcceptNullMethodImpl acceptNullMethod = new AcceptNullMethodImpl();
        RabbitConsumer rabbitConsumer2 = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, acceptNullMethod);

        Consumer<IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);
        remote.accept(null);

        synchronized (acceptNullMethod) {
            if (acceptNullMethod.getOk() != null && acceptNullMethod.getOk()) {
                assertTrue(acceptNullMethod.getOk());
            } else {
                acceptNullMethod.wait(500);
            }
        }
        assertTrue(acceptNullMethod.getOk());

        rabbitConsumer2.close();
    }
}