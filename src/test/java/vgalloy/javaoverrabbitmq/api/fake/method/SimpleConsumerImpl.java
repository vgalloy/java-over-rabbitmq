package vgalloy.javaoverrabbitmq.api.fake.method;

import vgalloy.javaoverrabbitmq.api.fake.message.IntegerMessage;

import java.util.function.Consumer;

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
