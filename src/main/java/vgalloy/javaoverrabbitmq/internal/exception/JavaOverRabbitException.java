package vgalloy.javaoverrabbitmq.internal.exception;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class JavaOverRabbitException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message the detail message
     */
    public JavaOverRabbitException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause the cause
     */
    public JavaOverRabbitException(Throwable cause) {
        super(cause);
    }
}
