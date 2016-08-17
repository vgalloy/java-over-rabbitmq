package vgalloy.javaoverrabbitmq.internal.queue;

import vgalloy.javaoverrabbitmq.api.message.RabbitMessage;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class SimpleQueueDefinition<M extends RabbitMessage> extends RPCQueueDefinition<M, RabbitMessage.None> {

    /**
     * Constructor.
     *
     * @param name                  the queue name
     * @param parameterMessageClass the class representing the message send
     */
    public SimpleQueueDefinition(String name, Class<M> parameterMessageClass) {
        super(name, parameterMessageClass, RabbitMessage.None.class);
    }
}
