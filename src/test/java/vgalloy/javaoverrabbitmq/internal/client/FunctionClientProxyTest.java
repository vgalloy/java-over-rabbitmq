package vgalloy.javaoverrabbitmq.internal.client;

import org.apache.qpid.server.Broker;
import org.junit.After;
import org.junit.Before;
import java.util.function.Function;

import com.rabbitmq.client.AlreadyClosedException;
import org.junit.Test;

import vgalloy.javaoverrabbitmq.api.Factory;
import vgalloy.javaoverrabbitmq.api.model.RabbitClientFunction;
import vgalloy.javaoverrabbitmq.api.model.RabbitElement;
import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.exception.TimeoutException;
import vgalloy.javaoverrabbitmq.utils.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.utils.fake.method.FunctionMethodImpl;
import vgalloy.javaoverrabbitmq.utils.util.BrokerUtils;
import vgalloy.javaoverrabbitmq.utils.util.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class FunctionClientProxyTest {

    private Broker broker;

    @Before
    public void tearUp() {
        broker = BrokerUtils.startEmbeddedBroker();
    }

    @After
    public void tearDown() {
        broker.shutdown();
    }

    @Test
    public void testSimpleRPC() {
        FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> fakeFunctionQueueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), DoubleIntegerMessage.class, IntegerMessage.class);
        RabbitElement rabbitConsumer = Factory.createConsumer(BrokerUtils.getConnectionFactory(), fakeFunctionQueueDefinition, new FunctionMethodImpl());

        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), fakeFunctionQueueDefinition);
        assertEquals(new IntegerMessage(3), remote.apply(new DoubleIntegerMessage(1, 2)));

        rabbitConsumer.close();
    }

    @Test
    public void testWithConsumerReachTimeout() throws Exception {
        // GIVEN
        Function<DoubleIntegerMessage, IntegerMessage> function = doubleIntegerMessage -> {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new IntegerMessage(doubleIntegerMessage.getFirst() + doubleIntegerMessage.getSecond());
        };
        FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), DoubleIntegerMessage.class, IntegerMessage.class);
        queueDefinition.setTimeoutMillis(1000);
        RabbitElement rabbitConsumer = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, function);
        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);
        Runnable runnable = () -> {
            try {
                remote.apply(new DoubleIntegerMessage(1, 2));
                fail("No TimeoutException");
            } catch (JavaOverRabbitException e) {
                assertTrue(e.getCause() instanceof TimeoutException);
            }
        };
        Thread thread = new Thread(runnable);

        // WHEN
        thread.start();

        // THEN
        try {
            Thread.sleep(200);
            assertTrue(thread.isAlive());
            Thread.sleep(900);
            assertFalse(thread.isAlive());
        } finally {
            rabbitConsumer.close();
        }
    }

    @Test
    public void testWithConsumerThrowError() throws Exception {
        // GIVEN
        Function<DoubleIntegerMessage, IntegerMessage> function = doubleIntegerMessage -> {
            throw new RuntimeException("Shit Happens");
        };
        FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), DoubleIntegerMessage.class, IntegerMessage.class);
        queueDefinition.setTimeoutMillis(1000);
        RabbitElement rabbitConsumer = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, function);
        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        // WHEN
        long start = System.currentTimeMillis();
        try {
            remote.apply(new DoubleIntegerMessage(1, 2));
            fail("No Exception ! ! ");
        } catch (JavaOverRabbitException e) {
            assertTrue(System.currentTimeMillis() - start < 500);
        } finally {
            rabbitConsumer.close();
        }
    }

    @Test
    public void testWithReturnNull() throws Exception {
        // GIVEN
        Function<DoubleIntegerMessage, IntegerMessage> function = doubleIntegerMessage -> null;
        FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), DoubleIntegerMessage.class, IntegerMessage.class);
        queueDefinition.setTimeoutMillis(1000);
        RabbitElement rabbitConsumer = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, function);
        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        // WHEN
        try {
            IntegerMessage result = remote.apply(new DoubleIntegerMessage(1, 2));
            assertNull(result);
        } finally {
            rabbitConsumer.close();
        }
    }

    @Test(expected = AlreadyClosedException.class)
    public void testCloseConsumer() {
        FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> fakeFunctionQueueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), DoubleIntegerMessage.class, IntegerMessage.class);
        RabbitElement rabbitConsumer = Factory.createConsumer(BrokerUtils.getConnectionFactory(), fakeFunctionQueueDefinition, new FunctionMethodImpl());

        rabbitConsumer.close();
        rabbitConsumer.getMessageCount();
    }

    @Test(expected = AlreadyClosedException.class)
    public void testCloseClient() {
        FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> fakeFunctionQueueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), DoubleIntegerMessage.class, IntegerMessage.class);
        RabbitClientFunction<?, ?> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), fakeFunctionQueueDefinition);

        remote.close();
        remote.getMessageCount();
    }
}
