package vgalloy.javaoverrabbitmq.utils.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
 */
public final class TestUtil {

    private static final Random RANDOM = ThreadLocalRandom.current();

    private TestUtil() {
        throw new AssertionError();
    }

    public static String getRandomQueueName() {
        return "TEST_QUEUE_" + RANDOM.nextInt();
    }
}
