package vgalloy.javaoverrabbitmq.internal.queue.impl;

import java.util.Objects;

import vgalloy.javaoverrabbitmq.api.queue.FunctionQueueDefinition;
import vgalloy.javaoverrabbitmq.internal.queue.AbstractQueueDefinition;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
 */
public final class FunctionQueueDefinitionImpl<P, R> extends AbstractQueueDefinition<P> implements FunctionQueueDefinition<P, R> {

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
        super(name, parameterMessageClass);
        this.returnMessageClass = Objects.requireNonNull(returnMessageClass);
    }

    @Override
    public Class<R> getReturnMessageClass() {
        return returnMessageClass;
    }

    @Override
    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    @Override
    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
}
