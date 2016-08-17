package vgalloy.javaoverrabbitmq.internal.exception;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class RabbitConsumerException {

    private final String message;

    /**
     * Constructor.
     *
     * @param message the message
     */
    public RabbitConsumerException(String message) {
        this.message = Objects.requireNonNull(message);
    }

    public String getMessage() {
        return message;
    }
}
