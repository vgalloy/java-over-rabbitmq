package vgalloy.javaoverrabbitmq.internal.queue;

import vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;
import vgalloy.javaoverrabbitmq.api.marshaller.impl.DefaultMarshaller;
import vgalloy.javaoverrabbitmq.api.queue.UntypedQueue;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 18/08/16.
 */
public abstract class AbstractQueueDefinition implements UntypedQueue {

    private final String name;
    private RabbitMessageMarshaller rabbitMessageMarshaller = DefaultMarshaller.INSTANCE;

    /**
     * Constructor.
     *
     * @param name the queue name
     */
    public AbstractQueueDefinition(String name) {
        this.name = Objects.requireNonNull(name);

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Queue name can not be empty");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setMarshaller(RabbitMessageMarshaller rabbitMessageMarshaller) {
        this.rabbitMessageMarshaller = Objects.requireNonNull(rabbitMessageMarshaller);
    }

    @Override
    public RabbitMessageMarshaller getMarshaller() {
        return rabbitMessageMarshaller;
    }
}
