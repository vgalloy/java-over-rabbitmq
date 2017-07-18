package com.vgalloy.javaoverrabbitmq.internal.queue;

import java.util.Objects;

import com.vgalloy.javaoverrabbitmq.api.queue.QueueDefinition;

/**
 * Created by Vincent Galloy on 11/12/16.
 *
 * @author Vincent Galloy
 */
public abstract class AbstractQueueDefinition<P> extends AbstractUntypedQueueDefinition implements QueueDefinition<P> {

    private final Class<P> parameterMessageClass;

    /**
     * Constructor.
     *
     * @param name                  the queue name
     * @param parameterMessageClass the class representing the message send
     */
    protected AbstractQueueDefinition(String name, Class<P> parameterMessageClass) {
        super(name);
        this.parameterMessageClass = Objects.requireNonNull(parameterMessageClass);
    }

    @Override
    public Class<P> getParameterMessageClass() {
        return parameterMessageClass;
    }
}
