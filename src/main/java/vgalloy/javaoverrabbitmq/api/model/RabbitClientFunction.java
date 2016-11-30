package vgalloy.javaoverrabbitmq.api.model;

import java.util.function.Function;

/**
 * @author Vincent Galloy - 30/11/16
 *         Created by Vincent Galloy on 30/11/16.
 */
public interface RabbitClientFunction<P, R> extends Function<P, R>, RabbitElement {

}
