package vgalloy.javaoverrabbitmq.api.fake.queue;

import vgalloy.javaoverrabbitmq.api.fake.message.DoubleIntegerMessage;
import vgalloy.javaoverrabbitmq.api.fake.message.IntegerMessage;
import vgalloy.javaoverrabbitmq.api.rpc.RPCQueue;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class DoubleMessageRPCQueue extends RPCQueue<DoubleIntegerMessage, IntegerMessage> {

    public DoubleMessageRPCQueue(String name) {
        super(name, DoubleIntegerMessage.class, IntegerMessage.class);
    }
}
