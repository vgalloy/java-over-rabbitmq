package vgalloy.javaoverrabbitmq.api.fake.method;

import vgalloy.javaoverrabbitmq.api.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.api.message.RabbitMessage;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class SimpleQueueMethodImpl implements RPCQueueMethod<IntegerMessage, RabbitMessage.None> {

    private int value = 0;

    @Override
    public RabbitMessage.None invoke(IntegerMessage integerMessage) {
        synchronized (this) {
            System.err.println("Je suis dans le lock (consumer)");

            System.err.println("Je set la valeur à  " + integerMessage.getFirst());
            value = integerMessage.getFirst();

            System.err.println("Je notifyAll");
            notifyAll();
        }

        System.err.println("Je suis sorti");
        return RabbitMessage.NONE;
    }

    public int getValue() {
        return value;
    }
}
