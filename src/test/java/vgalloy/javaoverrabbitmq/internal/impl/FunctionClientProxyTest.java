package vgalloy.javaoverrabbitmq.internal.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vgalloy.javaoverrabbitmq.api.Factory;
import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.api.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.api.fake.method.RPCQueueMethodImpl;
import vgalloy.javaoverrabbitmq.api.fake.method.SimpleQueueMethodImpl;
import vgalloy.javaoverrabbitmq.api.fake.util.Utils;
import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class FunctionClientProxyTest {

    private final FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> fakeFunctionQueueDefinition = Factory.createQueue("FAKE_QUEUE_NAME", DoubleIntegerMessage.class, IntegerMessage.class);
    private final ConsumerQueueDefinition<IntegerMessage> fakeFunctionQueueDefinition2 = Factory.createQueue("FAKE_QUEUE_NAME_2", IntegerMessage.class);

    private SimpleQueueMethodImpl simpleQueueMethod;
    private RabbitConsumer rabbitConsumer;
    private RabbitConsumer rabbitConsumer2;

    @Before
    public void tearUp() {
        simpleQueueMethod = new SimpleQueueMethodImpl();
        rabbitConsumer = Factory.createConsumer(Utils.getConnectionFactory(), fakeFunctionQueueDefinition, new RPCQueueMethodImpl());
        rabbitConsumer2 = Factory.createConsumer(Utils.getConnectionFactory(), fakeFunctionQueueDefinition2, simpleQueueMethod);
    }

    @After
    public void tearDown() {
        rabbitConsumer.close();
        rabbitConsumer2.close();
    }

    @Test
    public void simpleRPCTest() {
        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(Utils.getConnectionFactory(), fakeFunctionQueueDefinition);
        assertEquals(new IntegerMessage(3), remote.apply(new DoubleIntegerMessage(1, 2)));
    }

    @Test
    public void simpleTest() throws Exception {
        Consumer<IntegerMessage> remote = Factory.createClient(Utils.getConnectionFactory(), fakeFunctionQueueDefinition2);
        remote.accept(new IntegerMessage(1));

        synchronized (simpleQueueMethod) {
            if (simpleQueueMethod.getValue() == 1) {
                assertEquals(1, simpleQueueMethod.getValue());
            } else {
                simpleQueueMethod.wait(500);
            }
        }
        assertEquals(1, simpleQueueMethod.getValue());
    }
}
