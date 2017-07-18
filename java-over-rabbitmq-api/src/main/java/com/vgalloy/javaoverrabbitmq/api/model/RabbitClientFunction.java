package com.vgalloy.javaoverrabbitmq.api.model;

import java.util.function.Function;

/**
 * Created by Vincent Galloy on 30/11/16.
 *
 * @author Vincent Galloy
 */
public interface RabbitClientFunction<P, R> extends Function<P, R>, RabbitElement {

}
