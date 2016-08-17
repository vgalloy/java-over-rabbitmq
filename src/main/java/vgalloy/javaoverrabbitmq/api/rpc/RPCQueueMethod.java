package vgalloy.javaoverrabbitmq.api.rpc;

import vgalloy.javaoverrabbitmq.api.message.RabbitMessage;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public interface RPCQueueMethod<P extends RabbitMessage, R extends RabbitMessage> {

    /**
     * The method.
     *
     * @param p the message parameter
     * @return the message result
     */
    R invoke(P p);
}
