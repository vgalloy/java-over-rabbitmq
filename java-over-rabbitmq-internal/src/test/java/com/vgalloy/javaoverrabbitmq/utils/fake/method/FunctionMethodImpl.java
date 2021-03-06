package com.vgalloy.javaoverrabbitmq.utils.fake.method;

import java.util.function.Function;

import com.vgalloy.javaoverrabbitmq.utils.fake.message.DoubleIntegerMessage;
import com.vgalloy.javaoverrabbitmq.utils.fake.message.IntegerMessage;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
 */
public class FunctionMethodImpl implements Function<DoubleIntegerMessage, IntegerMessage> {

    @Override
    public IntegerMessage apply(DoubleIntegerMessage doubleIntegerMessage) {
        return new IntegerMessage(doubleIntegerMessage.getFirst() + doubleIntegerMessage.getSecond());
    }
}
