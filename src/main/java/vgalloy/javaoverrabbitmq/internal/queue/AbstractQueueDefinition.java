package vgalloy.javaoverrabbitmq.internal.queue;

import java.util.Objects;

import vgalloy.javaoverrabbitmq.api.queue.QueueDefinition;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 11/12/16.
 */
public abstract class AbstractQueueDefinition<P> extends AbstractUntypedQueueDefinition implements QueueDefinition<P> {

    private final Class<P> parameterMessageClass;

    /**
     * Constructor.
     *
     * @param name the queue name
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
