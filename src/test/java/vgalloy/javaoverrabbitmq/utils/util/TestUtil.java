package vgalloy.javaoverrabbitmq.utils.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Vincent Galloy on 17/08/16.
 *
 * @author Vincent Galloy
 */
public final class TestUtil {

    private static final Random RANDOM = new SecureRandom();

    private TestUtil() {
        throw new AssertionError();
    }

    public static String getRandomQueueName() {
        return "TEST_QUEUE_" + RANDOM.nextInt();
    }
}
