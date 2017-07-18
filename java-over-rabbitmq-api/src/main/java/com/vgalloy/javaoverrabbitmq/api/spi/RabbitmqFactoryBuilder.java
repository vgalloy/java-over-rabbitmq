package com.vgalloy.javaoverrabbitmq.api.spi;

import com.vgalloy.javaoverrabbitmq.api.factory.RabbitmqFactory;

/**
 * Created by Vincent Galloy on 18/07/17.
 *
 * @author Vincent Galloy
 */
public interface RabbitmqFactoryBuilder {

    /**
     * Build a {@link RabbitmqFactory}.
     *
     * @return a RabbitmqFactory
     */
    RabbitmqFactory build();
}
