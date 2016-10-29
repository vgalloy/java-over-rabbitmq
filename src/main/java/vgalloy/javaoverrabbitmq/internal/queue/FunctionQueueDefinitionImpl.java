package vgalloy.javaoverrabbitmq.internal.queue;

import java.util.Objects;

import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public final class FunctionQueueDefinitionImpl<P, R> extends AbstractQueueDefinition implements FunctionQueueDefinition<P, R> {

    private final Class<P> parameterMessageClass;
    private final Class<R> returnMessageClass;
    private long timeoutMillis = 1_000;

    /**
     * Constructor.
     *
     * @param name                  the queue name
     * @param parameterMessageClass the class representing the message send
     * @param returnMessageClass    the class representing the message received
     */
    public FunctionQueueDefinitionImpl(String name, Class<P> parameterMessageClass, Class<R> returnMessageClass) {
        super(name);
        this.parameterMessageClass = Objects.requireNonNull(parameterMessageClass);
        this.returnMessageClass = Objects.requireNonNull(returnMessageClass);
    }

    @Override
    public Class<P> getParameterMessageClass() {
        return parameterMessageClass;
    }

    @Override
    public Class<R> getReturnMessageClass() {
        return returnMessageClass;
    }

    @Override
    public long getTimeout() {
        return timeoutMillis;
    }

    @Override
    public void setTimeout(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
}
