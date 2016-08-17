package vgalloy.javaoverrabbitmq.api.fake.message;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class IntegerMessage {

    private final Integer first;

    public IntegerMessage(Integer first) {
        this.first = Objects.requireNonNull(first);
    }

    public Integer getFirst() {
        return first;
    }

    @Override
    public String toString() {
        return "IntegerMessage{" +
                "first=" + first +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegerMessage)) {
            return false;
        }

        IntegerMessage that = (IntegerMessage) o;

        return first != null ? first.equals(that.first) : that.first == null;
    }

    @Override
    public int hashCode() {
        return first != null ? first.hashCode() : 0;
    }
}
