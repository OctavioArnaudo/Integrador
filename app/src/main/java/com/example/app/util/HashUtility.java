package com.example.app.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class HashUtility {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * Custom exception for errors during hashing operations.
     */
    public static class HashingException extends Exception {
        public HashingException(String message) {
            super(message);
        }
    }

    /**
     * Custom exception for errors during salt generation.
     */
    public static class SaltException extends Exception {
        public SaltException(String message) {
            super(message);
        }
    }

    /**
     * Generates a random salt of 16 bytes using SecureRandom.
     *
     * @return The generated salt encoded in Base64.
     * @throws SaltException If an error occurs during salt generation.
     */
    public static String generateSalt() throws SaltException {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (Exception e) {
            throw new SaltException("Error generating salt.");
        }
    }

    /**
     * Hashes the given password with the provided salt using SHA-256.
     *
     * @param password The password to hash.
     * @param salt     The salt to use for hashing.
     * @return The Base64-encoded hash of the password and salt.
     * @throws HashingException If an error occurs during hashing.
     */
    public static String hashPassword(String password, String salt) throws HashingException {
        try {
            String passwordWithSalt = password + salt;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(passwordWithSalt.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new HashingException("Error hashing password.");
        }
    }

    /**
     * Verifies if the provided password matches the stored hash using the given salt.
     *
     * @param inputPassword The password to verify.
     * @param storedHash    The stored hash to compare against.
     * @param salt          The salt used for hashing.
     * @return true if the password matches the stored hash, false otherwise.
     * @throws HashingException If an error occurs during verification.
     */
    public static boolean checkPassword(String inputPassword, String storedHash, String salt) throws HashingException {
        String inputHash = hashPassword(inputPassword, salt);
        return inputHash.equals(storedHash);
    }

    /**
     * Encrypts the provided data using AES encryption and the specified salt as the key.
     *
     * @param data The data to encrypt.
     * @param salt The key used for encryption.
     * @return The Base64-encoded encrypted data.
     * @throws Exception If an error occurs during encryption.
     */
    public static String encrypt(String data, String salt) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(salt.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts the provided data using AES decryption and the specified salt as the key.
     *
     * @param encryptedData The Base64-encoded encrypted data to decrypt.
     * @param salt          The key used for decryption.
     * @return The decrypted data as a plain text string.
     * @throws Exception If an error occurs during decryption.
     */
    public static String decrypt(String encryptedData, String salt) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(salt.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    /**
     * Generates a secure random password containing uppercase, lowercase, digits, and special characters.
     *
     * @param length The length of the generated password.
     * @return The generated password.
     */
    public static String generateRandomPassword(int length) {
        final String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charset.length());
            password.append(charset.charAt(randomIndex));
        }

        return password.toString();
    }
    
}
