package vgalloy.javaoverrabbitmq.api.fake.method;

import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.api.fake.message.IntegerMessage;

import java.util.function.Function;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class RPCQueueMethodImpl implements Function<DoubleIntegerMessage, IntegerMessage> {

    @Override
    public IntegerMessage apply(DoubleIntegerMessage doubleIntegerMessage) {
        return new IntegerMessage(doubleIntegerMessage.getFirst() + doubleIntegerMessage.getSecond());
    }
}
