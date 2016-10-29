package vgalloy.javaoverrabbitmq.utils.fake.method;

import java.util.function.Consumer;

import vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class SimpleConsumerImpl implements Consumer<IntegerMessage> {

    private int value = 0;

    @Override
    public void accept(IntegerMessage integerMessage) {
        synchronized (this) {
            value = integerMessage.getFirst();
            notifyAll();
        }
    }

    public int getValue() {
        return value;
    }
}
