package org.jsd.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TestDataUtils {
    private static final Logger log = LogManager.getLogger(TestDataUtils.class);
    private static final Random random = new Random();

    // Generate random email
    public static String generateRandomEmail() {
        String email = "test" + System.currentTimeMillis() + "@example.com";
        log.info("Generated random email: {}", email);
        return email;
    }

    // Generate random phone number
    public static String generateRandomPhone() {
        String phone = "9" + String.format("%09d", random.nextInt(1000000000));
        log.info("Generated random phone: {}", phone);
        return phone;
    }

    // Generate random name
    public static String generateRandomName() {
        String[] firstNames = {"John", "Jane", "Mike", "Sarah", "David", "Lisa", "Tom", "Anna"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Davis", "Wilson", "Miller", "Taylor", "Anderson"};
        
        String name = firstNames[random.nextInt(firstNames.length)] + " " + 
                     lastNames[random.nextInt(lastNames.length)];
        log.info("Generated random name: {}", name);
        return name;
    }

    // Generate current timestamp
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }

    // Generate current date
    public static String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // Generate random string
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }
        log.info("Generated random string: {}", result.toString());
        return result.toString();
    }

    // Generate random number
    public static int generateRandomNumber(int min, int max) {
        int number = random.nextInt(max - min + 1) + min;
        log.info("Generated random number: {}", number);
        return number;
    }

    // Clean text (remove special characters)
    public static String cleanText(String text) {
        String cleaned = text.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
        log.info("Cleaned text: '{}' -> '{}'", text, cleaned);
        return cleaned;
    }

    // Format currency
    public static String formatCurrency(double amount) {
        String formatted = String.format("$%.2f", amount);
        log.info("Formatted currency: {} -> {}", amount, formatted);
        return formatted;
    }
}