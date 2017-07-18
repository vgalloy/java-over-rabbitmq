package com.vgalloy.javaoverrabbitmq.api.model;

import java.util.function.Consumer;

/**
 * Created by Vincent Galloy on 30/11/16.
 *
 * @author Vincent Galloy
 */
public interface RabbitClientConsumer<P> extends Consumer<P>, RabbitElement {

}
