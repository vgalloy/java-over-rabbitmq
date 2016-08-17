package vgalloy.javaoverrabbitmq.api.queue;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public interface QueueDefinition<P> {

    /**
     * Gets the name of the queue.
     *
     * @return the queue name
     */
    String getName();

    /**
     * Get the Java class representation of the parameter message.
     *
     * @return the parameter message class
     */
    Class<P> getParameterMessageClass();
}
