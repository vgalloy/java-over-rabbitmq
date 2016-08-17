package vgalloy.javaoverrabbitmq.internal.queue;

import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class FunctionQueueDefinitionImpl<P, R> implements FunctionQueueDefinition<P, R> {

    private final String name;
    private final Class<P> parameterMessageClass;
    private final Class<R> returnMessageClass;

    /**
     * Constructor.
     *
     * @param name                  the queue name
     * @param parameterMessageClass the class representing the message send
     * @param returnMessageClass    the class representing the message received
     */
    public FunctionQueueDefinitionImpl(String name, Class<P> parameterMessageClass, Class<R> returnMessageClass) {
        this.name = Objects.requireNonNull(name);
        this.parameterMessageClass = Objects.requireNonNull(parameterMessageClass);
        this.returnMessageClass = Objects.requireNonNull(returnMessageClass);

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

    @Override
    public Class<R> getReturnMessageClass() {
        return returnMessageClass;
    }
}
