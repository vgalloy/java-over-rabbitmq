package vgalloy.javaoverrabbitmq.api.model;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public interface RabbitElement {

    /**
     * Close the consumer's channel.
     */
    void close();

    /**
     * Get the number of message in the queue.
     *
     * @return the number of message
     */
    int getMessageCount();
}
