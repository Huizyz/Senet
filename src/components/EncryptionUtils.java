package components;

import java.util.Base64;

public class EncryptionUtils {

    private static final int SHIFT = 3; // shift for Caesar cipher

    // Encrypt the input string
    public static String encrypt(String input) {
        StringBuilder encrypted = new StringBuilder();
        for (char i : input.toCharArray()) {
            encrypted.append((char) (i + SHIFT));
        }
        return Base64.getEncoder().encodeToString(encrypted.toString().getBytes());
    }

    // Decrypt the input string
    public static String decrypt(String input) {
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        String decodedString = new String(decodedBytes);
        StringBuilder decrypted = new StringBuilder();
        for (char i : decodedString.toCharArray()) {
            decrypted.append((char) (i - SHIFT));
        }
        return decrypted.toString();
    }
}
