package vgalloy.javaoverrabbitmq.internal.client;

import org.junit.Test;
import vgalloy.javaoverrabbitmq.api.Factory;
import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.api.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.api.fake.method.FunctionMethodImpl;
import vgalloy.javaoverrabbitmq.api.fake.util.Utils;
import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.exception.JavaOverRabbitException;
import vgalloy.javaoverrabbitmq.util.TestUtil;

import java.util.function.Function;

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

    @Test
    public void testSimpleRPC() {
        FunctionQueueDefinition<DoubleIntegerMessage, IntegerMessage> fakeFunctionQueueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), DoubleIntegerMessage.class, IntegerMessage.class);
        RabbitConsumer rabbitConsumer = Factory.createConsumer(Utils.getConnectionFactory(), fakeFunctionQueueDefinition, new FunctionMethodImpl());

        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(Utils.getConnectionFactory(), fakeFunctionQueueDefinition);
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
        queueDefinition.setTimeout(1000);
        RabbitConsumer rabbitConsumer = Factory.createConsumer(Utils.getConnectionFactory(), queueDefinition, function);
        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(Utils.getConnectionFactory(), queueDefinition);
        Runnable runnable = () -> remote.apply(new DoubleIntegerMessage(1, 2));
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
        queueDefinition.setTimeout(1000);
        RabbitConsumer rabbitConsumer = Factory.createConsumer(Utils.getConnectionFactory(), queueDefinition, function);
        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(Utils.getConnectionFactory(), queueDefinition);

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
        queueDefinition.setTimeout(1000);
        RabbitConsumer rabbitConsumer = Factory.createConsumer(Utils.getConnectionFactory(), queueDefinition, function);
        Function<DoubleIntegerMessage, IntegerMessage> remote = Factory.createClient(Utils.getConnectionFactory(), queueDefinition);

        // WHEN
        try {
            IntegerMessage result = remote.apply(new DoubleIntegerMessage(1, 2));
            assertNull(result);
        } finally {
            rabbitConsumer.close();
        }
    }
}
