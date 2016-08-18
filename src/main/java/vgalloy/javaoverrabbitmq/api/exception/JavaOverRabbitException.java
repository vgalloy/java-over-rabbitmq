package vgalloy.javaoverrabbitmq.api.exception;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class JavaOverRabbitException extends RuntimeException {

    private static final long serialVersionUID = 4967432216041009952L;

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

    /**
     * Constructor.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public JavaOverRabbitException(String message, Throwable cause) {
        super(message, cause);
    }
}
