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
  private static final String ALPHA_NUMERIC_CHARS =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int RANDOM_STRING_LENGTH = 10;

  public static String generatePublicId(String input) {
    String lowerCaseInput = input.toLowerCase();
    String stripped = lowerCaseInput.replaceAll("[^a-zA-Z0-9\\s]", "");
    String replaced = stripped.replaceAll("\\s+", "-");
    String randomString = HashUtils.generateRandomString();
    return replaced + "-" + randomString;
  }

  private static String generateRandomString() {
    Random random = new Random();
    StringBuilder sb = new StringBuilder(RANDOM_STRING_LENGTH);

    for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
      int index = random.nextInt(ALPHA_NUMERIC_CHARS.length());
      char randomChar = ALPHA_NUMERIC_CHARS.charAt(index);
      sb.append(randomChar);
    }

    return sb.toString();
  }

  public static String hashWithSalt(String input, String salt)
    throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(
      (salt + input).getBytes(StandardCharsets.UTF_8)
    );
    return Base64.getEncoder().encodeToString(hash);
  }

  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] saltBytes = new byte[SALT_LENGTH];
    random.nextBytes(saltBytes);
    return Base64.getEncoder().encodeToString(saltBytes);
  }

  public static boolean verify(String input, String salt, String expectedHash)
    throws NoSuchAlgorithmException {
    String actualHash = hashWithSalt(input, salt);
    return actualHash.equals(expectedHash);
  }

  public static String generateUUID() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }
}
