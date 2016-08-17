package vgalloy.javaoverrabbitmq.api.fake.message;

import java.util.Objects;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 */
public class DoubleIntegerMessage {

    private final Integer first;
    private final Integer second;

    public DoubleIntegerMessage(Integer first, Integer second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    public Integer getFirst() {
        return first;
    }

    public Integer getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "DoubleIntegerMessage{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoubleIntegerMessage)) {
            return false;
        }

        DoubleIntegerMessage that = (DoubleIntegerMessage) o;

        if (first != null ? !first.equals(that.first) : that.first != null) {
            return false;
        }
        return second != null ? second.equals(that.second) : that.second == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
