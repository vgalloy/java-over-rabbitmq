package com.vgalloy.javaoverrabbitmq.internal.exception;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Vincent Galloy on 17/08/16.
 * This class wrap the message given by the remote method invocation error.
 * This class is not a real {@link java.lang.RuntimeException}. It design for serialization and will be deserialize by the client marshaller.
 *
 * @author Vincent Galloy
 */
public final class RabbitConsumerException implements Serializable {

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
