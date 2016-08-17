package vgalloy.javaoverrabbitmq.api.message;

/**
 * @author Vincent Galloy
 *         Created by Vincent Galloy on 15/08/16.
 *         This class reprensent all the message.
 */
public interface RabbitMessage {

    None NONE = new None();

    class None implements RabbitMessage {

    }
}
