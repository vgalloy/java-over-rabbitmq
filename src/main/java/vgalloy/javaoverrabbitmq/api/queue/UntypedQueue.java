package vgalloy.javaoverrabbitmq.api.queue;

import vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 18/08/16.
 */
public interface UntypedQueue {

    /**
     * Gets the name of the queue.
     *
     * @return the queue name
     */
    String getName();

    /**
     * Get the marshaller.
     *
     * @return the marshaller
     */
    RabbitMessageMarshaller getMarshaller();

    /**
     * Set a marshaller for this queue. Can not be null. For default marshaller use {@link vgalloy.javaoverrabbitmq.api.marshaller.impl.DefaultMarshaller}
     *
     * @param rabbitMessageMarshaller the marshaller
     */
    void setMarshaller(RabbitMessageMarshaller rabbitMessageMarshaller);
}
