package vgalloy.javaoverrabbitmq.internal.queue.impl;

import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.queue.AbstractQueueDefinition;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class ConsumerQueueDefinitionImpl<P> extends AbstractQueueDefinition<P> implements ConsumerQueueDefinition<P> {

    /**
     * Constructor.
     *
     * @param name                  the queue name
     * @param parameterMessageClass the class representing the message send
     */
    public ConsumerQueueDefinitionImpl(String name, Class<P> parameterMessageClass) {
        super(name, parameterMessageClass);
    }
}
