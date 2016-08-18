package vgalloy.javaoverrabbitmq.internal.exception;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class RabbitConsumerException implements Serializable {

    public static final String ERROR_HEADER = "isError";
    private static final long serialVersionUID = 5571744443841837849L;

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
