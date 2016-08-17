package vgalloy.javaoverrabbitmq.api.queue;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public interface FunctionQueueDefinition<P, R> extends QueueDefinition<P> {

    /**
     * Get the Java class representation of the return message.
     *
     * @return the return message class
     */
    Class<R> getReturnMessageClass();
}
