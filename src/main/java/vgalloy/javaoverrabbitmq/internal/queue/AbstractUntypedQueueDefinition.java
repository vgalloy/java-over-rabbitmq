package vgalloy.javaoverrabbitmq.internal.queue;

import java.util.Objects;

import vgalloy.javaoverrabbitmq.api.marshaller.RabbitMessageMarshaller;
import vgalloy.javaoverrabbitmq.api.marshaller.impl.DefaultMarshaller;
import vgalloy.javaoverrabbitmq.api.queue.UntypedQueue;

/**
 * Created by Vincent Galloy on 18/08/16.
 *
 * @author Vincent Galloy
 */
public abstract class AbstractUntypedQueueDefinition implements UntypedQueue {

    private final String name;
    private RabbitMessageMarshaller rabbitMessageMarshaller = DefaultMarshaller.INSTANCE;

    /**
     * Constructor.
     *
     * @param name the queue name
     */
    protected AbstractUntypedQueueDefinition(String name) {
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
    public RabbitMessageMarshaller getMarshaller() {
        return rabbitMessageMarshaller;
    }

    @Override
    public void setMarshaller(RabbitMessageMarshaller rabbitMessageMarshaller) {
        this.rabbitMessageMarshaller = Objects.requireNonNull(rabbitMessageMarshaller);
    }
}
