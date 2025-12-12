package services;

import account.Account;
import account.AccountManager;
import account.SavingsAccount;
import account.CheckingAccount;
import customer.Customer;
import customer.CustomerManager;
import customer.RegularCustomer;
import customer.PremiumCustomer;
import transaction.Transaction;
import transaction.TransactionManager;
import utils.CustomUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FilePersistenceService {
    private static final String DATASET_DIR = "dataset";
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String DELIMITER = "\\|";

    private final AccountManager accountManager;
    private final CustomerManager customerManager;
    private final TransactionManager transactionManager;

    private Map<String, Customer> loadedCustomers = new HashMap<>();

    public FilePersistenceService(AccountManager accountManager,
                                  CustomerManager customerManager,
                                  TransactionManager transactionManager) {
        this.accountManager = accountManager;
        this.customerManager = customerManager;
        this.transactionManager = transactionManager;
        createDatasetDirectory();
    }

    private void createDatasetDirectory() {
        try {
            Path datasetPath = Paths.get(DATASET_DIR);
            if (!Files.exists(datasetPath)) {
                Files.createDirectories(datasetPath);
            }
        } catch (IOException e) {
            CustomUtils.printError("Failed to create dataset directory: " + e.getMessage());
        }
    }

    public void saveAllData() {
        try {
            int accountsSaved = saveAccounts();
            int customersSaved = saveCustomers();
            int transactionsSaved = saveTransactions();

            CustomUtils.printSuccess("Data saved successfully!");
            CustomUtils.print("Accounts saved: " + accountsSaved);
            CustomUtils.print("Customers saved: " + customersSaved);
            CustomUtils.print("Transactions saved: " + transactionsSaved);
        } catch (IOException e) {
            CustomUtils.printError("Failed to save data: " + e.getMessage());
        }
    }

    public void loadAllData() {
        try {
            int customersLoaded = loadCustomers();
            int accountsLoaded = loadAccounts();
            int transactionsLoaded = loadTransactions();

            CustomUtils.printSuccess("Data loaded successfully!");
            CustomUtils.print("Customers loaded: " + customersLoaded);
            CustomUtils.print("Accounts loaded: " + accountsLoaded);
            CustomUtils.print("Transactions loaded: " + transactionsLoaded);
        } catch (IOException e) {
            CustomUtils.printError("Failed to load data: " + e.getMessage());
        }
    }

    public int saveAccounts() throws IOException {
        Path filePath = Paths.get(DATASET_DIR, ACCOUNTS_FILE);
        List<Account> accounts = accountManager.getAccounts();
        int count = 0;

        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Account account : accounts) {
                String line = formatAccountLine(account);
                writer.write(line);
                writer.newLine();
                count++;
            }
        }

        return count;
    }

    public int saveCustomers() throws IOException {
        Path filePath = Paths.get(DATASET_DIR, CUSTOMERS_FILE);
        List<Customer> customers = customerManager.getAllCustomers();
        int count = 0;

        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Customer customer : customers) {
                String line = formatCustomerLine(customer);
                writer.write(line);
                writer.newLine();
                count++;
            }
        }

        return count;
    }

    public int saveTransactions() throws IOException {
        Path filePath = Paths.get(DATASET_DIR, TRANSACTIONS_FILE);
        List<Transaction> transactions = transactionManager.getAllTransactions();
        int count = 0;

        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Transaction transaction : transactions) {
                String line = formatTransactionLine(transaction);
                writer.write(line);
                writer.newLine();
                count++;
            }
        }

        return count;
    }

    public int loadAccounts() throws IOException {
        Path filePath = Paths.get(DATASET_DIR, ACCOUNTS_FILE);
        if (!Files.exists(filePath)) {
            return 0;
        }

        int count = 0;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                Account account = parseAccountLine(line);
                if (account != null) {
                    accountManager.addAccount(account);
                    count++;
                }
            }
        }

        return count;
    }

    public int loadCustomers() throws IOException {
        Path filePath = Paths.get(DATASET_DIR, CUSTOMERS_FILE);
        if (!Files.exists(filePath)) {
            return 0;
        }

        int count = 0;
        loadedCustomers.clear();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                Customer customer = parseCustomerLine(line);
                if (customer != null) {
                    loadedCustomers.put(customer.getCustomerId(), customer);
                    count++;
                }
            }
        }

        return count;
    }

    public int loadTransactions() throws IOException {
        Path filePath = Paths.get(DATASET_DIR, TRANSACTIONS_FILE);
        if (!Files.exists(filePath)) {
            return 0;
        }

        int count = 0;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                Transaction transaction = parseTransactionLine(line);
                if (transaction != null) {
                    transactionManager.addTransaction(transaction);
                    count++;
                }
            }
        }

        return count;
    }

    private Account parseAccountLine(String line) {
        try {
            String[] parts = line.split(DELIMITER);
            if (parts.length < 8 || !parts[0].equals("ACCOUNT")) {
                return null;
            }

            String accountNumber = parts[1];
            String accountType = parts[2];
            String customerId = parts[3];
            double balance = Double.parseDouble(parts[4]);
            String status = parts[5];

            Customer customer = loadedCustomers.get(customerId);
            if (customer == null) {
                CustomUtils.printError("Customer not found for account: " + accountNumber);
                return null;
            }

            Account account;
            if (accountType.equals("Savings")) {
                account = new SavingsAccount(customer, balance);

            } else {
                account = new CheckingAccount(customer, balance);
            }

            account.setStatus(status);
            return account;

        } catch (Exception e) {
            CustomUtils.printError("Failed to parse account line: " + e.getMessage());
            return null;
        }
    }

    private Customer parseCustomerLine(String line) {
        try {
            String[] parts = line.split(DELIMITER);
            if (parts.length < 7 || !parts[0].equals("CUSTOMER")) {
                return null;
            }

            String name = parts[2];
            int age = Integer.parseInt(parts[3]);
            String contact = parts[4];
            String address = parts[5];
            String customerType = parts[6];

            Customer customer;
            if (customerType.equals("Premium")) {
                customer = new PremiumCustomer(name, age, contact, address);
            } else {
                customer = new RegularCustomer(name, age, contact, address);
            }

            return customer;

        } catch (Exception e) {
            CustomUtils.printError("Failed to parse customer line: " + e.getMessage());
            return null;
        }
    }

    private Transaction parseTransactionLine(String line) {
        try {
            String[] parts = line.split(DELIMITER);
            if (parts.length < 7 || !parts[0].equals("TRANSACTION")) {
                return null;
            }

            String accountNumber = parts[2];
            String type = parts[3];
            double amount = Double.parseDouble(parts[4]);
            double balanceAfter = Double.parseDouble(parts[5]);
            String timestamp = parts[6];

            Transaction transaction;
            if (parts.length > 7) {
                String relatedAccount = parts[7];
                transaction = new Transaction(accountNumber, type, amount, balanceAfter, relatedAccount);
            } else {
                transaction = new Transaction(accountNumber, type, amount, balanceAfter);
            }

            return transaction;

        } catch (Exception e) {
            CustomUtils.printError("Failed to parse transaction line: " + e.getMessage());
            return null;
        }
    }

    private String formatAccountLine(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append("ACCOUNT").append("|");
        sb.append(account.getAccountNumber()).append("|");
        sb.append(account.getAccountType()).append("|");
        sb.append(account.getCustomer().getCustomerId()).append("|");
        sb.append(String.format("%.2f", account.getBalance())).append("|");
        sb.append(account.getStatus()).append("|");

        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            sb.append(savings.getInterestRate()).append("|");
            sb.append(String.format("%.2f", savings.getMinimumBalance()));
        } else if (account instanceof CheckingAccount) {
            CheckingAccount checking = (CheckingAccount) account;
            sb.append(String.format("%.2f", checking.getOverdraftLimit())).append("|");
            sb.append(String.format("%.2f", checking.getMonthlyFee()));
        }

        return sb.toString();
    }

    private String formatCustomerLine(Customer customer) {
        return "CUSTOMER" + "|" +
                customer.getCustomerId() + "|" +
                customer.getName() + "|" +
                customer.getAge() + "|" +
                customer.getContact() + "|" +
                customer.getAddress() + "|" +
                customer.getCustomerType();
    }

    private String formatTransactionLine(Transaction transaction) {
        return "TRANSACTION" + "|" +
                transaction.getTransactionId() + "|" +
                transaction.getAccountNumber() + "|" +
                transaction.getType() + "|" +
                String.format("%.2f", transaction.getAmount()) + "|" +
                String.format("%.2f", transaction.getBalanceAfter()) + "|" +
                transaction.getTimestamp();
    }

    public void saveAccountsOnly() {
        try {
            int count = saveAccounts();
            CustomUtils.printSuccess("✓ Accounts saved successfully!");
            CustomUtils.print("Accounts saved: " + count);
        } catch (IOException e) {
            CustomUtils.printError("Failed to save accounts: " + e.getMessage());
        }
    }

    public void saveCustomersOnly() {
        try {
            int count = saveCustomers();
            CustomUtils.printSuccess("✓ Customers saved successfully!");
            CustomUtils.print("Customers saved: " + count);
        } catch (IOException e) {
            CustomUtils.printError("Failed to save customers: " + e.getMessage());
        }
    }

    public void saveTransactionsOnly() {
        try {
            int count = saveTransactions();
            CustomUtils.printSuccess("✓ Transactions saved successfully!");
            CustomUtils.print("Transactions saved: " + count);
        } catch (IOException e) {
            CustomUtils.printError("Failed to save transactions: " + e.getMessage());
        }
    }

    public boolean dataFilesExist() {
        Path accountsPath = Paths.get(DATASET_DIR, ACCOUNTS_FILE);
        Path customersPath = Paths.get(DATASET_DIR, CUSTOMERS_FILE);
        Path transactionsPath = Paths.get(DATASET_DIR, TRANSACTIONS_FILE);

        return Files.exists(accountsPath) &&
                Files.exists(customersPath) &&
                Files.exists(transactionsPath);
    }

    public boolean anyDataFileExists() {
        Path accountsPath = Paths.get(DATASET_DIR, ACCOUNTS_FILE);
        Path customersPath = Paths.get(DATASET_DIR, CUSTOMERS_FILE);
        Path transactionsPath = Paths.get(DATASET_DIR, TRANSACTIONS_FILE);

        return Files.exists(accountsPath) ||
                Files.exists(customersPath) ||
                Files.exists(transactionsPath);
    }
}