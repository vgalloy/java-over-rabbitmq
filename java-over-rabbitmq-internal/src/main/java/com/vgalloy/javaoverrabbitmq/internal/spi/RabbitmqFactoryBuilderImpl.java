package com.vgalloy.javaoverrabbitmq.internal.spi;

import com.vgalloy.javaoverrabbitmq.api.factory.RabbitmqFactory;
import com.vgalloy.javaoverrabbitmq.api.spi.RabbitmqFactoryBuilder;
import com.vgalloy.javaoverrabbitmq.internal.factory.RabbitmqFactoryImpl;

/**
 * Created by Vincent Galloy on 18/07/17.
 *
 * @author Vincent Galloy
 */
public final class RabbitmqFactoryBuilderImpl implements RabbitmqFactoryBuilder {

    @Override
    public RabbitmqFactory build() {
        return new RabbitmqFactoryImpl();
    }
}
