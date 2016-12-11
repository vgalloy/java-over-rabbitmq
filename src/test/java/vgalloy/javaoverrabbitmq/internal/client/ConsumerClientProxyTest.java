package vgalloy.javaoverrabbitmq.internal.client;

import java.util.function.Consumer;

import org.apache.qpid.server.Broker;
import org.junit.After;
import org.junit.Before;
import java.util.function.Consumer;

import com.rabbitmq.client.AlreadyClosedException;
import org.junit.Assert;
import org.junit.Test;

import vgalloy.javaoverrabbitmq.api.Factory;
import vgalloy.javaoverrabbitmq.api.model.RabbitClientConsumer;
import vgalloy.javaoverrabbitmq.api.model.RabbitElement;
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
public class ConsumerClientProxyTest {

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
    public void testSimple() throws Exception {
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        SimpleConsumerImpl simpleQueueMethod = new SimpleConsumerImpl(null);
        RabbitElement rabbitConsumer2 = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, simpleQueueMethod);
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
        RabbitElement rabbitConsumer2 = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, simpleQueueMethod);
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
        SlowConsumerImpl slowConsumer = new SlowConsumerImpl();
        RabbitElement rabbitConsumer2 = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, slowConsumer);
        RabbitClientConsumer<IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        Assert.assertEquals(0, rabbitConsumer2.getMessageCount());
        remote.accept(new IntegerMessage(1));
        remote.accept(new IntegerMessage(1));
        Thread.sleep(100);
        Assert.assertTrue(0 < rabbitConsumer2.getMessageCount());
        Assert.assertTrue(0 < remote.getMessageCount());
        remote.accept(new IntegerMessage(1));
        remote.accept(new IntegerMessage(1));
        Assert.assertTrue(3 < rabbitConsumer2.getMessageCount());
        Assert.assertTrue(3 < remote.getMessageCount());

        rabbitConsumer2.close();
        remote.close();
    }

    @Test(expected = AlreadyClosedException.class)
    public void testCloseConsumer() throws Exception {
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        SimpleConsumerImpl simpleQueueMethod = new SimpleConsumerImpl(null);
        RabbitElement rabbitConsumer2 = Factory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, simpleQueueMethod);

        rabbitConsumer2.close();
        rabbitConsumer2.getMessageCount();
    }

    @Test(expected = AlreadyClosedException.class)
    public void testCloseClient() throws Exception {
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = Factory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        RabbitClientConsumer<IntegerMessage> remote = Factory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        remote.close();
        remote.getMessageCount();
    }
}