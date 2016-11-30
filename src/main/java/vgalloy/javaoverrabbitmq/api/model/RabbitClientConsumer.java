package vgalloy.javaoverrabbitmq.api.model;

import java.util.function.Consumer;

/**
 * @author Vincent Galloy - 30/11/16
 *         Created by Vincent Galloy on 30/11/16.
 */
public interface RabbitClientConsumer<P> extends Consumer<P>, RabbitElement {

}
