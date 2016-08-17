package vgalloy.javaoverrabbitmq.internal.exception;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class TimeoutException extends JavaOverRabbitException {

    /**
     * Constructor.
     *
     * @param message the detail message
     */
    public TimeoutException(String message) {
        super(message);
    }
}
