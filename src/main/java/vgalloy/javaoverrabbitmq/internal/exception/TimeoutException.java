package vgalloy.javaoverrabbitmq.internal.exception;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class TimeoutException extends JavaOverRabbitException {

    private static final long serialVersionUID = 4034451795858748708L;

    /**
     * Constructor.
     *
     * @param message the detail message
     */
    public TimeoutException(String message) {
        super(message);
    }
}
