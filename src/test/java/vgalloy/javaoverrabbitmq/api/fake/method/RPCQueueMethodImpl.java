package vgalloy.javaoverrabbitmq.api.fake.method;

import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.api.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueueMethod;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class RPCQueueMethodImpl implements RPCQueueMethod<DoubleIntegerMessage, IntegerMessage> {

    @Override
    public IntegerMessage invoke(DoubleIntegerMessage doubleIntegerMessage) {
        return new IntegerMessage(doubleIntegerMessage.getFirst() + doubleIntegerMessage.getSecond());
    }
}
