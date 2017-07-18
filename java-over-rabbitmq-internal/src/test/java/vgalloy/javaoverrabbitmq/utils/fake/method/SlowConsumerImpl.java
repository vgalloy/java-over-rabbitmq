package vgalloy.javaoverrabbitmq.utils.fake.method;

import java.util.function.Consumer;

import vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
 */
public class SlowConsumerImpl implements Consumer<IntegerMessage> {

    @Override
    public void accept(IntegerMessage integerMessage) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
