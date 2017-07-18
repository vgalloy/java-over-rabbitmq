package com.vgalloy.javaoverrabbitmq.internal.queue.impl;

import com.vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;
import com.vgalloy.javaoverrabbitmq.internal.queue.AbstractQueueDefinition;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
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
