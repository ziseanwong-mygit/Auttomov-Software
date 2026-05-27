package main.java.backend.util;

import main.java.backend.model.UserAccount;
import main.java.records.PasswordHash;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PswdHasher {

    // Private constructor to prevent instantiation
    private PswdHasher() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    // Generate a hash for the new password with its unique salt
    // For signing up new accounts or changing password
    public static PasswordHash hashNewPassword(String newPassword) {
        byte[] saltBytes = generateSalt();
        String newSalt = Base64.getEncoder().encodeToString(saltBytes);

        String newHash = computeHash(newPassword, saltBytes);

        return new PasswordHash(newHash, newSalt);
    }

    // Generate a new salt for password hashing; a non-secret data that uniquely hashes password
    // Without salt, two matching passwords would have same hash
    private static byte[] generateSalt()  {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    // Verify the input password against the existing hash during login
    public static boolean verifyPassword(String password, UserAccount account) {

        // Get the existing hash and salt from the account
        String pswdSalt = account.getPasswordHash().hashSalt();
        String pswdHash = account.getPasswordHash().pswdHash();

        // Compute hashing for password input
        byte[] saltBytes = Base64.getDecoder().decode(pswdSalt);
        String hashedPswdInput = PswdHasher.computeHash(password, saltBytes);

        // If the input hash matches the existing hash, returns true
        return hashedPswdInput.equals(pswdHash);
    }

    // Perform hashing on the given password and salt
    // Used for generating new hash or comparing passwords for authentication
    private static String computeHash(String password, byte[] salt) {

        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("PBKDF2WithHmacSHA1 algorithm not found");

        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Incorrect key specification provided");
        }

    }
}
