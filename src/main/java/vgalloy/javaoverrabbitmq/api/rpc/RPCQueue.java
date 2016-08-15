package vgalloy.javaoverrabbitmq.api.rpc;

import vgalloy.javaoverrabbitmq.api.RabbitMessage;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public abstract class RPCQueue<P extends RabbitMessage, R extends RabbitMessage> {

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
    public RPCQueue(String name, Class<P> parameterMessageClass, Class<R> returnMessageClass) {
        this.name = Objects.requireNonNull(name);
        this.parameterMessageClass = Objects.requireNonNull(parameterMessageClass);
        this.returnMessageClass = Objects.requireNonNull(returnMessageClass);

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("RPCQueue name can not be empty");
        }
    }

    public String getName() {
        return name;
    }

    public Class<P> getParameterMessageClass() {
        return parameterMessageClass;
    }

    public Class<R> getReturnMessageClass() {
        return returnMessageClass;
    }
}
