package vgalloy.javaoverrabbitmq.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.api.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.api.fake.method.RPCQueueMethodImpl;
import vgalloy.javaoverrabbitmq.api.fake.queue.DoubleMessageRPCQueue;
import vgalloy.javaoverrabbitmq.api.fake.util.Utils;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueue;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;

import static org.junit.Assert.assertEquals;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class ClientTest {

    private final RPCQueueMethod<DoubleIntegerMessage, IntegerMessage> fakeQueueMethod = new RPCQueueMethodImpl();
    private final RPCQueue<DoubleIntegerMessage, IntegerMessage> fakeQueue = new DoubleMessageRPCQueue("FAKE_QUEUE_NAME");
    private RabbitConsumer rabbitConsumer;

    @Before
    public void tearUp() {
        rabbitConsumer = Factory.createConsumer(Utils.getConnectionFactory(), fakeQueue, fakeQueueMethod);
    }

    @After
    public void tearDown() {
        rabbitConsumer.close();
    }

    @Test
    public void simpleTest() {
        RPCQueueMethod<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(Utils.getConnectionFactory(), fakeQueue);
        assertEquals(new IntegerMessage(3), remote.invoke(new DoubleIntegerMessage(1, 2)));
    }
}
