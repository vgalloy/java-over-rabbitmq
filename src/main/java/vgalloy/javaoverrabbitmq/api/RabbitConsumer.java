package vgalloy.javaoverrabbitmq.api;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public interface RabbitConsumer {

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
