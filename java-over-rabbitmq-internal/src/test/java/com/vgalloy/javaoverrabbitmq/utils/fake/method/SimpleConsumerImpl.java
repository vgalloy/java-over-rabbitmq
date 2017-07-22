package com.vgalloy.javaoverrabbitmq.utils.fake.method;

import java.util.function.Consumer;

import com.vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
 */
public class SimpleConsumerImpl implements Consumer<IntegerMessage> {

    private IntegerMessage result;

    public SimpleConsumerImpl(IntegerMessage result) {
        this.result = result;
    }

    @Override
    public synchronized void accept(IntegerMessage integerMessage) {
        result = integerMessage;
    }

    public IntegerMessage getResult() {
        return result;
    }
}
