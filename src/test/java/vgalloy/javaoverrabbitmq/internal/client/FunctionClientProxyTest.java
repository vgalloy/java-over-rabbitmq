package vgalloy.javaoverrabbitmq.internal.client;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import com.rabbitmq.client.AlreadyClosedException;
import org.apache.qpid.server.Broker;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import vgalloy.javaoverrabbitmq.api.Factory;
import vgalloy.javaoverrabbitmq.api.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.api.model.RabbitClientFunction;
import vgalloy.javaoverrabbitmq.api.model.RabbitElement;
import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.utils.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.utils.fake.method.FunctionMethodImpl;
import vgalloy.javaoverrabbitmq.utils.util.BrokerUtils;
import vgalloy.javaoverrabbitmq.utils.util.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
 */
public class FunctionClientProxyTest {

    private static Broker broker;

    @BeforeClass
    public static void tearUp() {
        broker = BrokerUtils.startEmbeddedBroker();
    }

    @AfterClass
    public static void tearDown() {
        broker.shutdown();
    }

    @Test
    public void testSimpleRPC() {
        // GIVEN
        FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> fakeFunctionQueueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), DoubleIntegerMessage.class, IntegerMessage.class);
        RabbitElement rabbitConsumer = Factory.createConsumer(BrokerUtils.getConnectionFactory(), fakeFunctionQueueDefinition, new FunctionMethodImpl());
        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), fakeFunctionQueueDefinition);

        // WHEN
        IntegerMessage result = remote.apply(new DoubleIntegerMessage(1, 2));

        // THEN
        assertEquals(new IntegerMessage(3), result);

        // FINALLY
        rabbitConsumer.close();
    }

    @Test
    public void testWithConsumerReachTimeout() throws Exception {
        // GIVEN
        Function<DoubleIntegerMessage, IntegerMessage> function = doubleIntegerMessage -> {
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            return new IntegerMessage(doubleIntegerMessage.getFirst() + doubleIntegerMessage.getSecond());
        };
        FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), DoubleIntegerMessage.class, IntegerMessage.class);
        queueDefinition.setTimeoutMillis(1000);
        RabbitElement rabbitConsumer = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, function);
        Function<DoubleIntegerMessage, IntegerMessage> client = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);
        Runnable runnable = () -> {
            try {
                client.apply(new DoubleIntegerMessage(1, 2));
                fail("No TimeoutException");
            } catch (JavaOverRabbitException expected) {
                throw new RuntimeException();
            }
        };
        Thread thread = new Thread(runnable);

        // WHEN
        thread.start();

        // THEN
        try {
            Thread.sleep(200);
            assertTrue(thread.isAlive());
            Thread.sleep(2_000);
            Assert.assertFalse(thread.isAlive());
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

    @Test
    public void testMultiThread() throws Exception {
        // GIVEN
        Random random = ThreadLocalRandom.current();
        Function<Integer, Integer> function = e -> {
                try {
                    Thread.sleep(Math.abs(random.nextInt() % 10));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            return 2 * e;
        };
        FunctionQueueDefinition<Integer, Integer> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), Integer.class, Integer.class);
        Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, function);

        Runnable runnable = () -> {
            Function<Integer, Integer> client = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);
            for (int i = 0; i < 10; i++) {
                int value = Math.abs(random.nextInt() % 100);
                int result = client.apply(value);
                Assert.assertEquals(2 * value, result);
            }
        };

        // WHEN
        CompletableFuture run1 = CompletableFuture.runAsync(runnable);
        CompletableFuture run2 = CompletableFuture.runAsync(runnable);
        CompletableFuture run3 = CompletableFuture.runAsync(runnable);

        // THEN
        run1.get();
        run2.get();
        run3.get();
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
