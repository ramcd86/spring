package services.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

public class HashUtils {

    private static final int SALT_LENGTH = 16;

    // CAN REPLACE SALT WITH STATIC VALUE FOR TEST
    public static String hash(String input) throws NoSuchAlgorithmException {
        String salt = generateSalt();
        return hashWithSalt(input, salt);
    }

    public static String hashWithSalt(String input, String salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest((salt + input).getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[SALT_LENGTH];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public static boolean verify(String input, String salt, String expectedHash) throws NoSuchAlgorithmException {
        String actualHash = hashWithSalt(input, salt);
        return actualHash.equals(expectedHash);
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String generateRandomString() {
        final int length = 20;
        final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            char randomChar = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));

            if (random.nextBoolean()) {
                randomChar = Character.toUpperCase(randomChar);
            }

            sb.append(randomChar);
        }

        return sb.toString();
    }

}
