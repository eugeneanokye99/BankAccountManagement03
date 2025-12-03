package utils;

import exceptions.ValidationException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InputValidator {

    // Name validation with exception
    public static String validateName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name cannot be empty", "name");
        }

        String trimmedName = name.trim();

        // Check minimum length
        if (trimmedName.length() < 2) {
            throw new ValidationException("Name must be at least 2 characters", "name");
        }

        // Check for numbers
        if (Pattern.compile(".*\\d.*").matcher(trimmedName).matches()) {
            throw new ValidationException("Name cannot contain numbers", "name");
        }

        // Check for only letters and spaces
        if (!Pattern.compile("^[a-zA-Z\\s]+$").matcher(trimmedName).matches()) {
            throw new ValidationException("Name can only contain letters and spaces", "name");
        }

        return trimmedName;
    }

    // Age validation with exception
    public static int validateAge(String ageInput) throws ValidationException {
        try {
            int age = Integer.parseInt(ageInput.trim());

            if (age < 18) {
                throw new ValidationException("Customer must be at least 18 years old", "age");
            }

            if (age > 120) {
                throw new ValidationException("Please enter a valid age (max 120)", "age");
            }

            return age;
        } catch (NumberFormatException e) {
            throw new ValidationException("Age must be a valid number", "age");
        }
    }

    // Contact validation with exception
    public static String validateContact(String contact) throws ValidationException {
        if (contact == null || contact.trim().isEmpty()) {
            throw new ValidationException("Contact information is required", "contact");
        }

        String trimmedContact = contact.trim();

        // Basic phone validation
        if (!Pattern.compile("^[+]?[\\d\\s\\-\\(\\)]+$").matcher(trimmedContact).matches()) {
            throw new ValidationException("Please enter a valid contact number", "contact");
        }

        // Minimum length check
        if (trimmedContact.length() < 7) {
            throw new ValidationException("Contact number is too short (minimum 7 digits)", "contact");
        }

        return trimmedContact;
    }

    // Address validation with exception
    public static String validateAddress(String address) throws ValidationException {
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException("Address is required", "address");
        }

        String trimmedAddress = address.trim();

        if (trimmedAddress.length() < 5) {
            throw new ValidationException("Please enter a complete address (minimum 5 characters)", "address");
        }

        return trimmedAddress;
    }

    // Amount validation with exception
    public static double validateAmount(String amountInput) throws ValidationException {
        try {
            double amount = Double.parseDouble(amountInput.trim());

            if (amount <= 0) {
                throw new ValidationException("Amount must be greater than zero", "amount");
            }

            // Check decimal places (max 2)
            if (amountInput.contains(".")) {
                String decimalPart = amountInput.split("\\.")[1];
                if (decimalPart.length() > 2) {
                    throw new ValidationException("Amount cannot have more than 2 decimal places", "amount");
                }
            }

            return amount;
        } catch (NumberFormatException e) {
            throw new ValidationException("Please enter a valid amount", "amount");
        }
    }

    // Menu choice validation with exception
    public static int validateChoice(String choiceInput, int min, int max) throws ValidationException {
        try {
            int choice = Integer.parseInt(choiceInput.trim());

            if (choice < min || choice > max) {
                throw new ValidationException(
                        String.format("Please enter a number between %d and %d", min, max),
                        "choice");
            }

            return choice;
        } catch (NumberFormatException e) {
            throw new ValidationException("Please enter a valid number", "choice");
        }
    }

    // Confirmation validation with exception
    public static String validateConfirmation(String input) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException("Please enter Y or N", "confirmation");
        }

        String trimmed = input.trim().toLowerCase();
        if (!trimmed.equals("y") && !trimmed.equals("n")) {
            throw new ValidationException("Please enter Y or N", "confirmation");
        }

        return trimmed;
    }

    // Get valid input with prompt and validation (wrapped version for Main.java)
    public static String getValidInput(Scanner scanner, String prompt, ValidationRule validator, String errorMessage) {
        while (true) {
            CustomUtils.printInline(prompt);
            String input = scanner.nextLine().trim();

            if (validator.isValid(input)) {
                return input;
            }

            CustomUtils.printError(errorMessage);
        }
    }

    // Get valid integer input
    public static int getValidInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            CustomUtils.printInline(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                CustomUtils.printError("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                CustomUtils.printError("Please enter a valid number");
            }
        }
    }

    // Get valid double input
    public static double getValidDouble(Scanner scanner, String prompt, double min) {
        while (true) {
            CustomUtils.printInline(prompt);
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                if (value >= min) {
                    return value;
                }
                CustomUtils.printError("Amount must be at least $" + min);
            } catch (NumberFormatException e) {
                CustomUtils.printError("Please enter a valid amount");
            }
        }
    }

    // Interface for custom validation rules
    public interface ValidationRule {
        boolean isValid(String input);
    }

    // Pre-defined validation rules
    public static class ValidationRules {
        public static final ValidationRule NAME_RULE = InputValidator::isValidName;
        public static final ValidationRule AGE_RULE = InputValidator::isValidAge;
        public static final ValidationRule CONTACT_RULE = InputValidator::isValidContact;
        public static final ValidationRule ADDRESS_RULE = InputValidator::isValidAddress;
        public static final ValidationRule AMOUNT_RULE = InputValidator::isValidAmount;
        public static final ValidationRule ACCOUNT_NUMBER_RULE = InputValidator::isValidAccountNumber;
    }

    // Helper validation methods
    public static boolean isValidName(String name) {
        try {
            validateName(name);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    public static boolean isValidAge(String ageInput) {
        try {
            validateAge(ageInput);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    public static boolean isValidContact(String contact) {
        try {
            validateContact(contact);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    public static boolean isValidAddress(String address) {
        try {
            validateAddress(address);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    public static boolean isValidAmount(String amountInput) {
        try {
            validateAmount(amountInput);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    public static boolean isValidConfirmation(String input) {
        try {
            validateConfirmation(input);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && Pattern.compile("^ACC\\d{3}$").matcher(accountNumber.trim()).matches();
    }
}