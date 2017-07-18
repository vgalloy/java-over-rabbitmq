package vgalloy.javaoverrabbitmq.api.queue;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
 */
public interface QueueDefinition<P> extends UntypedQueue {

    /**
     * Get the Java class representation of the parameter message.
     *
     * @return the parameter message class
     */
    Class<P> getParameterMessageClass();
}
