package vgalloy.javaoverrabbitmq.internal.queue;

import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class ConsumerQueueDefinitionImpl<P> extends AbstractQueueDefinition implements ConsumerQueueDefinition<P> {

    private final Class<P> parameterMessageClass;

    /**
     * Constructor.
     *
     * @param name                  the queue name
     * @param parameterMessageClass the class representing the message send
     */
    public ConsumerQueueDefinitionImpl(String name, Class<P> parameterMessageClass) {
        super(name);
        this.parameterMessageClass = Objects.requireNonNull(parameterMessageClass);
    }

    @Override
    public Class<P> getParameterMessageClass() {
        return parameterMessageClass;
    }
}
