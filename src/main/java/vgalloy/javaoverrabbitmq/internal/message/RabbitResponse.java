package vgalloy.javaoverrabbitmq.internal.message;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class RabbitResponse {

    private final byte[] objectAsByteArray;
    private final boolean isError;

    /**
     * Constructor.
     *
     * @param objectAsByteArray the message as a byte array
     * @param isError           true if the message is an error
     */
    public RabbitResponse(byte[] objectAsByteArray, boolean isError) {
        this.objectAsByteArray = Objects.requireNonNull(objectAsByteArray);
        this.isError = Objects.requireNonNull(isError);
    }

    public byte[] getObjectAsByteArray() {
        return objectAsByteArray;
    }

    public boolean isError() {
        return isError;
    }
}
