package vgalloy.javaoverrabbitmq.internal.exception;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class RabbitRemoteException extends RuntimeException {

    private static final long serialVersionUID = -9096381537997104025L;

    /**
     * Constructor.
     *
     * @param rabbitConsumerException the rabbit consumer exception
     */
    public RabbitRemoteException(RabbitConsumerException rabbitConsumerException) {
        super(Objects.requireNonNull(rabbitConsumerException).getMessage());
    }
}
