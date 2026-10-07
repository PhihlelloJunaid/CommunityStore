package za.ac.cput.communitystore.util;

import java.security.SecureRandom;

public final class UserIdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UserIdGenerator() {
    }

    public static String customerId() {
        return String.valueOf(100000000 + RANDOM.nextInt(900000000));
    }

    public static String staffId(String prefix) {
        return prefix + "-" + (100000 + RANDOM.nextInt(900000));
    }
}