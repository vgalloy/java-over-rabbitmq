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
import vgalloy.javaoverrabbitmq.api.message.RabbitMessage;
import vgalloy.javaoverrabbitmq.api.queue.QueueDefinition;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;

import static org.junit.Assert.assertEquals;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class ClientProxyTest {

    private final QueueDefinition<DoubleIntegerMessage, IntegerMessage> fakeQueueDefinition = Factory.createQueue("FAKE_QUEUE_NAME", DoubleIntegerMessage.class, IntegerMessage.class);
    private final QueueDefinition<IntegerMessage, RabbitMessage.None> fakeQueueDefinition2 = Factory.createQueue("FAKE_QUEUE_NAME_2", IntegerMessage.class);

    private SimpleQueueMethodImpl simpleQueueMethod;
    private RabbitConsumer rabbitConsumer;
    private RabbitConsumer rabbitConsumer2;

    @Before
    public void tearUp() {
        simpleQueueMethod = new SimpleQueueMethodImpl();
        rabbitConsumer = Factory.createConsumer(Utils.getConnectionFactory(), fakeQueueDefinition, new RPCQueueMethodImpl());
        rabbitConsumer2 = Factory.createConsumer(Utils.getConnectionFactory(), fakeQueueDefinition2, simpleQueueMethod);
    }

    @After
    public void tearDown() {
        rabbitConsumer.close();
        rabbitConsumer2.close();
    }

    @Test
    public void simpleRPCTest() {
        RPCQueueMethod<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(Utils.getConnectionFactory(), fakeQueueDefinition);
        assertEquals(new IntegerMessage(3), remote.invoke(new DoubleIntegerMessage(1, 2)));
    }

    @Test
    public void simpleTest() throws Exception {
        RPCQueueMethod<IntegerMessage, RabbitMessage.None> remote = Factory.createClient(Utils.getConnectionFactory(), fakeQueueDefinition2);
        remote.invoke(new IntegerMessage(1));

        synchronized (simpleQueueMethod) {
            System.err.println("Je suis dans le lock");
            if (simpleQueueMethod.getValue() == 1) {
                System.err.println("Je suis bon");
                assertEquals(1, simpleQueueMethod.getValue());
            } else {
                System.err.println("Je wait ...");
                simpleQueueMethod.wait(500);
            }
        }
        assertEquals(1, simpleQueueMethod.getValue());
    }
}
