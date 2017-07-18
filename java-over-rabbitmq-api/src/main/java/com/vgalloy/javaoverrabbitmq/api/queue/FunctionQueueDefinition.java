package com.vgalloy.javaoverrabbitmq.api.queue;

/**
 * Created by Vincent Galloy on 15/08/16.
 *
 * @author Vincent Galloy
 */
public interface FunctionQueueDefinition<P, R> extends QueueDefinition<P> {

    /**
     * Get the Java class representation of the return message.
     *
     * @return the return message class
     */
    Class<R> getReturnMessageClass();

    /**
     * Get the timeout.
     *
     * @return the timeout
     */
    long getTimeoutMillis();

    /**
     * Set the timeout.
     *
     * @param timeoutMillis the time before an exception be raise.
     */
    void setTimeoutMillis(long timeoutMillis);
}
