package vgalloy.javaoverrabbitmq.api.model;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
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
