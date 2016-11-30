package vgalloy.javaoverrabbitmq.internal.client;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import vgalloy.javaoverrabbitmq.api.Factory;
import vgalloy.javaoverrabbitmq.api.RabbitConsumer;
import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.utils.fake.method.SimpleConsumerImpl;
import vgalloy.javaoverrabbitmq.utils.fake.method.SlowConsumerImpl;
import vgalloy.javaoverrabbitmq.utils.util.BrokerUtils;
import vgalloy.javaoverrabbitmq.utils.util.TestUtil;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class ConsumerClientProxyITest {

    @Test
    public void testSimple() throws Exception {
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        SimpleConsumerImpl simpleQueueMethod = new SimpleConsumerImpl(null);
        RabbitConsumer rabbitConsumer2 = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, simpleQueueMethod);
        Consumer<IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        // WHEN
        remote.accept(new IntegerMessage(1));

        synchronized (simpleQueueMethod) {
            if (simpleQueueMethod.getResult() == null) {
                simpleQueueMethod.wait(300);
            }
        }

        // THEN
        Assert.assertEquals(new Integer(1), simpleQueueMethod.getResult().getFirst());
        rabbitConsumer2.close();
    }

    @Test
    public void testNullAsArgument() throws Exception {
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        SimpleConsumerImpl simpleQueueMethod = new SimpleConsumerImpl(new IntegerMessage(1));
        RabbitConsumer rabbitConsumer2 = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, simpleQueueMethod);
        Consumer<IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        // WHEN
        remote.accept(null);

        synchronized (simpleQueueMethod) {
            if (simpleQueueMethod.getResult() != null) {
                simpleQueueMethod.wait(300);
            }
        }

        // THEN
        Assert.assertNull(simpleQueueMethod.getResult());
        rabbitConsumer2.close();
    }

    @Test
    public void testNumberMessageInQueue() throws Exception {
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        SlowConsumerImpl noConsumer = new SlowConsumerImpl();
        RabbitConsumer rabbitConsumer2 = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, noConsumer);
        Consumer<IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        Assert.assertEquals(0, rabbitConsumer2.getMessageCount());
        remote.accept(new IntegerMessage(1));
        remote.accept(new IntegerMessage(1));
        Assert.assertEquals(1, rabbitConsumer2.getMessageCount());
        remote.accept(new IntegerMessage(1));
        remote.accept(new IntegerMessage(1));
        Assert.assertEquals(3, rabbitConsumer2.getMessageCount());
    }
}