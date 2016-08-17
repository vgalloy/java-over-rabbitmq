package vgalloy.javaoverrabbitmq.internal.queue;

import vgalloy.javaoverrabbitmq.api.message.RabbitMessage;
import vgalloy.javaoverrabbitmq.api.queue.QueueDefinition;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class RPCQueueDefinition<P extends RabbitMessage, R extends RabbitMessage> implements QueueDefinition<P, R> {

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
    public RPCQueueDefinition(String name, Class<P> parameterMessageClass, Class<R> returnMessageClass) {
        this.name = Objects.requireNonNull(name);
        this.parameterMessageClass = Objects.requireNonNull(parameterMessageClass);
        this.returnMessageClass = Objects.requireNonNull(returnMessageClass);

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("QueueDefinition name can not be empty");
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
