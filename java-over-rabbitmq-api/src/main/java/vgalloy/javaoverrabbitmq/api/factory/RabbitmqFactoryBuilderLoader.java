package vgalloy.javaoverrabbitmq.api.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import vgalloy.javaoverrabbitmq.api.spi.RabbitmqFactoryBuilder;

/**
 * Created by Vincent Galloy on 18/07/17.
 *
 * @author Vincent Galloy
 */
enum RabbitmqFactoryBuilderLoader {
    INSTANCE;

    /**
     * Load an instance of {@link RabbitmqFactoryBuilder}.
     *
     * @return a RabbitmqFactoryBuilder
     */
    RabbitmqFactoryBuilder load() {
        ServiceLoader<RabbitmqFactoryBuilder> serviceLoader = ServiceLoader.load(RabbitmqFactoryBuilder.class);
        serviceLoader.reload();
        List<RabbitmqFactoryBuilder> rabbitmqFactoryBuilders = new ArrayList<>();

        serviceLoader.iterator().forEachRemaining(rabbitmqFactoryBuilders::add);

        if (rabbitmqFactoryBuilders.isEmpty()) {
            throw new IllegalStateException("No implementation found for : " + RabbitmqFactoryBuilder.class.getName());
        } else if (rabbitmqFactoryBuilders.size() > 1) {
            throw new IllegalStateException("More than one implementation found for : " + RabbitmqFactoryBuilder.class.getName());
        }
        return rabbitmqFactoryBuilders.get(0);
    }
}
