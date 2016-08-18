package vgalloy.javaoverrabbitmq.utils.fake.method;

import vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;

import java.util.function.Consumer;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 17/08/16.
 */
public class AcceptNullMethodImpl implements Consumer<IntegerMessage> {

    private Boolean ok;

    @Override
    public void accept(IntegerMessage integerMessage) {
        synchronized (this) {
            ok = integerMessage == null;
            notifyAll();
        }
    }

    public Boolean getOk() {
        return ok;
    }
}