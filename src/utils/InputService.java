package utils;

import exceptions.ValidationException;
import java.util.Scanner;
import java.util.function.Function;

public final class InputService {

    private final Scanner scanner;

    public InputService(Scanner scanner) {
        this.scanner = scanner;
    }

    // Generic input with validation
    public String getInputWithValidation(
            String prompt,
            Function<String, String> validator) {

        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                // Validator returns cleaned/processed input or throws ValidationException
                return validator.apply(input);
            } catch (RuntimeException e) {
                // Handle wrapped ValidationExceptions
                if (e.getCause() instanceof ValidationException) {
                    System.out.println("Error: " + e.getCause().getMessage());
                    System.out.println("Please try again.\n");
                } else {
                    throw e; // Re-throw actual runtime exceptions
                }
            }
        }
    }

    // Specific input methods using InputValidator
    public String getName() {
        return getInputWithValidation(
                "Enter name: ",
                input -> {
                    try {
                        InputValidator.validateName(input);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                    return input.trim();
                }
        );
    }

    public int getAge() {
        while (true) {
            System.out.print("Enter age: ");
            String input = scanner.nextLine().trim();

            try {
                InputValidator.validateNumericString(input, "Age");
                int age = Integer.parseInt(input);
                InputValidator.validateAge(age);
                return age;
            } catch (ValidationException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.\n");
            }
        }
    }

    public double getPositiveAmount() {
        while (true) {
            System.out.print("Enter amount: ");
            String input = scanner.nextLine().trim();

            try {
                InputValidator.validateNumericString(input, "Amount");
                double amount = Double.parseDouble(input);
                InputValidator.validateAmount(amount);
                return amount;
            } catch (ValidationException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.\n");
            }
        }
    }

    public boolean getConfirmation(String message) {
        while (true) {
            System.out.print(message + " (Y/N): ");
            String input = scanner.nextLine().trim();

            try {
                InputValidator.validateConfirmation(input);
                return input.equalsIgnoreCase("Y");
            } catch (ValidationException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.\n");
            }
        }
    }

    // For integer input within range
    public int getIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                InputValidator.validateNumericString(input, "Selection");
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Error: Please enter a number between " + min + " and " + max);
                System.out.println("Please try again.\n");
            } catch (ValidationException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.\n");
            }
        }
    }

    // For positive double input with custom prompt
    public double getPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                InputValidator.validateNumericString(input, "Amount");
                double amount = Double.parseDouble(input);
                InputValidator.validateAmount(amount);
                return amount;
            } catch (ValidationException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.\n");
            }
        }
    }



    // For contact input
    public String getContact() {
        return getInputWithValidation(
                "Enter contact number: ",
                input -> {
                    try {
                        InputValidator.validateContact(input);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                    return input.trim();
                }
        );
    }



    // For account number input without format restriction
    public String getAccountNumber(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println("Error: Account number cannot be empty");
            System.out.println("Please try again.\n");
        }
    }


}