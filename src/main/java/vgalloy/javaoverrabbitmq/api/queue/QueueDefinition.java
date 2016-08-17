package vgalloy.javaoverrabbitmq.api.queue;

import vgalloy.javaoverrabbitmq.api.message.RabbitMessage;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public interface QueueDefinition<P extends RabbitMessage, R extends RabbitMessage> {

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

    /**
     * Get the Java class representation of the return message.
     *
     * @return the return message class
     */
    Class<R> getReturnMessageClass();
}
