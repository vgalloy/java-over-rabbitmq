package vgalloy.javaoverrabbitmq.internal.queue;

import vgalloy.javaoverrabbitmq.api.queue.ConsumerQueueDefinition;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public final class ConsumerQueueDefinitionImpl<P> implements ConsumerQueueDefinition<P> {

    private final String name;
    private final Class<P> parameterMessageClass;

    /**
     * Constructor.
     *
     * @param name                  the queue name
     * @param parameterMessageClass the class representing the message send
     */
    public ConsumerQueueDefinitionImpl(String name, Class<P> parameterMessageClass) {
        this.name = Objects.requireNonNull(name);
        this.parameterMessageClass = Objects.requireNonNull(parameterMessageClass);

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("FunctionQueueDefinitionImpl name can not be empty");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<P> getParameterMessageClass() {
        return parameterMessageClass;
    }
}
