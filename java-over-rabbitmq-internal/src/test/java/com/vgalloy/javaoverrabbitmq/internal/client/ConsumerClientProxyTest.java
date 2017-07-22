package com.vgalloy.javaoverrabbitmq.internal.client;

import java.util.function.Consumer;

import com.rabbitmq.client.AlreadyClosedException;
import org.apache.qpid.server.Broker;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.vgalloy.javaoverrabbitmq.api.factory.RabbitmqFactory;
import com.vgalloy.javaoverrabbitmq.api.model.RabbitClientConsumer;
import com.vgalloy.javaoverrabbitmq.api.model.RabbitElement;
import com.vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import com.vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;
import com.vgalloy.javaoverrabbitmq.utils.fake.method.SimpleConsumerImpl;
import com.vgalloy.javaoverrabbitmq.utils.fake.method.SlowConsumerImpl;
import com.vgalloy.javaoverrabbitmq.utils.util.BrokerUtils;
import com.vgalloy.javaoverrabbitmq.utils.util.TestUtil;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
 */
public final class ConsumerClientProxyTest {

    private static Broker broker;

    @BeforeClass
    public static void tearUp() {
        broker = BrokerUtils.startEmbeddedBroker();
    }

    @AfterClass
    public static void tearDown() {
        broker.shutdown();
    }

    private final RabbitmqFactory rabbitmqFactory = RabbitmqFactory.getInstance();

    @Test
    public void testSimple() throws Exception {
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = rabbitmqFactory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        SimpleConsumerImpl simpleQueueMethod = new SimpleConsumerImpl(null);
        RabbitElement rabbitConsumer2 = rabbitmqFactory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, simpleQueueMethod);
        Consumer<IntegerMessage> remote = rabbitmqFactory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        // WHEN
        remote.accept(new IntegerMessage(1));

        Thread.sleep(300);

        // THEN
        Assert.assertEquals(Integer.valueOf(1), simpleQueueMethod.getResult().getFirst());
        rabbitConsumer2.close();
    }

    @Test
    public void testNullAsArgument() throws Exception {
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = rabbitmqFactory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        SimpleConsumerImpl simpleQueueMethod = new SimpleConsumerImpl(new IntegerMessage(1));
        RabbitElement rabbitConsumer2 = rabbitmqFactory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, simpleQueueMethod);
        Consumer<IntegerMessage> remote = rabbitmqFactory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        // WHEN
        remote.accept(null);

        Thread.sleep(300);

        // THEN
        Assert.assertNull(simpleQueueMethod.getResult());
        rabbitConsumer2.close();
    }

    @Test
    public void testNumberMessageInQueue() throws Exception {
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = rabbitmqFactory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        SlowConsumerImpl slowConsumer = new SlowConsumerImpl();
        RabbitElement rabbitConsumer2 = rabbitmqFactory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, slowConsumer);
        RabbitClientConsumer<IntegerMessage> remote = rabbitmqFactory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

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
        // GIVEN
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = rabbitmqFactory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        SimpleConsumerImpl simpleQueueMethod = new SimpleConsumerImpl(null);
        RabbitElement rabbitConsumer2 = rabbitmqFactory.createConsumer(BrokerUtils.getConnectionFactory(), queueDefinition, simpleQueueMethod);

        // WHEN
        rabbitConsumer2.close();
        rabbitConsumer2.getMessageCount();

        // THEN
        Assert.fail("Exception should occurred");
    }

    @Test(expected = AlreadyClosedException.class)
    public void testCloseClient() throws Exception {
        ConsumerQueueDefinition<IntegerMessage> queueDefinition = rabbitmqFactory.createQueue(TestUtil.getRandomQueueName(), IntegerMessage.class);
        RabbitClientConsumer<IntegerMessage> remote = rabbitmqFactory.createClient(BrokerUtils.getConnectionFactory(), queueDefinition);

        // WHEN
        remote.close();
        remote.getMessageCount();

        // THEN
        Assert.fail("Exception should occurred");
    }
}