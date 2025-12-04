package utils;

import exceptions.ValidationException;
import java.util.regex.Pattern;

public final class InputValidator {

    private InputValidator() {}

    // Name validation
    public static void validateName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }

        String trimmedName = name.trim();

        if (trimmedName.length() < 2) {
            throw new ValidationException("Name must be at least 2 characters");
        }

        if (Pattern.compile(".*\\d.*").matcher(trimmedName).matches()) {
            throw new ValidationException("Name cannot contain numbers");
        }

        if (!Pattern.compile("^[a-zA-Z\\s]+$").matcher(trimmedName).matches()) {
            throw new ValidationException("Name can only contain letters and spaces");
        }
    }

    // Age validation
    public static void validateAge(int age) throws ValidationException {
        if (age < 18) {
            throw new ValidationException("Customer must be at least 18 years old");
        }
        if (age > 120) {
            throw new ValidationException("Please enter a valid age (max 120)");
        }
    }

    // Contact validation
    public static void validateContact(String contact) throws ValidationException {
        if (contact == null || contact.trim().isEmpty()) {
            throw new ValidationException("Contact information is required");
        }

        String trimmedContact = contact.trim();

        if (!Pattern.compile("^[+]?[\\d\\s\\-\\(\\)]+$").matcher(trimmedContact).matches()) {
            throw new ValidationException("Please enter a valid contact number");
        }

        if (trimmedContact.length() < 7) {
            throw new ValidationException("Contact number is too short (minimum 7 digits)");
        }
    }

    // Address validation
    public static void validateAddress(String address) throws ValidationException {
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException("Address is required");
        }

        if (address.trim().length() < 5) {
            throw new ValidationException("Please enter a complete address (minimum 5 characters)");
        }
    }

    // Amount validation
    public static void validateAmount(double amount) throws ValidationException {
        if (amount <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }
    }

    // Confirmation validation
    public static void validateConfirmation(String input) throws ValidationException {
        if (input == null) {
            throw new ValidationException("Input cannot be null");
        }

        String trimmed = input.trim().toLowerCase();
        if (!trimmed.equals("y") && !trimmed.equals("n")) {
            throw new ValidationException("Please enter Y or N");
        }
    }

    // Numeric input validation
    public static void validateNumericString(String input, String fieldName) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }

        try {
            Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid number");
        }
    }


}