# Bank Account Management System

A comprehensive console-based banking application built with Java that demonstrates modern Java features including Collections, Functional Programming, File I/O, and Concurrency. This project evolved from Week 2 to include advanced Java 21 features while maintaining all existing functionality.

## 🚀 Enhanced Features (Week 3 Updates)

### Core Modern Java Features

#### **1. Collections Migration with Functional Programming**
- **Replaced Arrays**: Migrated from arrays to `ArrayList` and `HashMap<String, Account>`
- **Functional Streams**: Implemented Streams API for data processing
- **Lambda Expressions**: Used for concise filtering, mapping, and sorting
- **Method References**: Applied for cleaner code in functional operations

#### **2. File Persistence with Functional Stream Processing**
- **Data Persistence**: Save all accounts and transactions to text files
- **Autoload on Startup**: Automatically load data from `accounts.txt` and `transactions.txt`
- **Java NIO**: Use of `Files` and `Paths` APIs for modern file handling
- **Functional I/O**: Process file data using Streams and Method References

#### **3. Regex Validation**
- **Input Validation**: Regex patterns for account numbers (`ACC\d{3}`), emails, phone numbers
- **User-Friendly Errors**: Clear error messages for invalid input formats
- **Centralized Logic**: Validation rules in `ValidationUtils` class
- **Predicate Lambdas**: Optional dynamic validation rules

#### **4. Thread-Safe Concurrent Transactions**
- **Concurrency Simulation**: Multiple threads executing simultaneous deposits/withdrawals
- **Thread Safety**: `synchronized` methods to prevent race conditions
- **Parallel Processing**: Optionally use parallel streams for batch operations
- **Real-time Logging**: Display thread activities in console

#### **5. Enhanced Console Experience**
- **Load/Save Confirmation**: Clear messages for file operations
- **Real-time Thread Logs**: Monitor concurrent operations
- **Readable Output**: Maintain clean, formatted console display

### Retained Core Functionality
- **Account Management**: Create, view, search, and manage bank accounts
- **Customer Management**: Support for Regular and Premium customers with detailed views
- **Transaction Processing**: Deposit, withdrawal, and inter-account transfer operations
- **Transaction History**: View complete transaction records with summaries
- **Menu Navigation**: Hierarchical menu system with user-friendly interface

### Account Types
- **Savings Account**:
    - Earns 3.5% annual interest
    - Minimum balance requirement: $500
    - Interest calculation functionality
    - Minimum balance enforcement on withdrawals

- **Checking Account**:
    - $1,000 overdraft limit
    - $10 monthly fee (waived for Premium customers)
    - Overdraft protection with limit enforcement
    - Monthly fee application with premium customer waivers

## 🛡️ Exception Handling System
- **Custom Exception Hierarchy**: 10+ specific exception types
- **Business Logic Exceptions**:
    - `InsufficientFundsException` - When withdrawal exceeds balance
    - `OverdraftLimitExceededException` - When overdraft limit is exceeded
    - `MinimumBalanceViolationException` - For savings account minimum balance
- **Input Validation**: `ValidationException` for user input errors
- **Graceful Error Recovery**: User-friendly messages with recovery options

## 🎨 Modular UI Architecture
- **AccountUI**: Handles all account viewing and searching operations
- **CustomerUI**: Manages customer listing and detailed views
- **AccountManagerUI**: Administrative functions
- **Main.java**: Clean orchestration layer with minimal business logic

## ✅ Comprehensive Input Validation
- **Format Validation**: Names, contacts, amounts with proper formatting
- **Business Validation**: Age ranges, minimum deposits, balance requirements
- **Real-time Feedback**: Immediate error correction with helpful messages
- **Validation Rules**: Reusable validation rules via functional interfaces

## 🛠️ Technology Stack

- **Language**: Java 21 (LTS)
- **Build Tool**: Maven 3.6+
- **Testing**: JUnit 5, Mockito
- **Paradigm**: Object-Oriented Programming with Functional Programming
- **Design Patterns**: MVC separation, Factory pattern, Strategy pattern
- **Data Structures**: `ArrayList`, `HashMap` for efficient data management
- **Functional Features**: Streams API, Lambda Expressions, Method References
- **File I/O**: Java NIO (`Files`, `Paths`)
- **Concurrency**: Threads, `synchronized` methods, parallel streams

## 📋 Prerequisites

- Java Development Kit (JDK) 21 or higher
- Apache Maven 3.6 or higher
- IntelliJ IDEA Community Edition (recommended) or any Java IDE
- Git (for version control)

## ⚙️ Installation & Setup

### Method 1: Using Maven (Recommended)

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/BankAccountManagement.git
   cd BankAccountManagement
   ```

2. **Build the Project**
   ```bash
   # Clean and compile
   mvn clean compile
   
   # Run the application
   mvn exec:java -Dexec.mainClass="Main"
   
   # Run tests
   mvn test
   
   # Create executable JAR
   mvn clean package
   ```

3. **Run from JAR**
   ```bash
   java -jar target/BankAccountManagement-1.0.jar
   ```

### Method 2: Using IntelliJ IDEA

1. **Open Project**
    - Open IntelliJ IDEA
    - Select "Open" → Choose the project folder
    - Select "Open as Project"
    - Let IDEA configure Maven automatically

2. **Run Configuration**
    - Main Class: `Main`
    - Working Directory: Project root
    - JDK: 21 or higher

## 📁 Project Structure

```
BankAccountManagement/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── account/           # Account-related classes
│   │   │   │   ├── Account.java (abstract)
│   │   │   │   ├── SavingsAccount.java
│   │   │   │   ├── CheckingAccount.java
│   │   │   │   └── AccountManager.java (UPDATED with Collections & Streams)
│   │   │   ├── customer/          # Customer-related classes
│   │   │   │   ├── Customer.java (abstract)
│   │   │   │   ├── RegularCustomer.java
│   │   │   │   └── PremiumCustomer.java
│   │   │   ├── transaction/       # Transaction-related classes
│   │   │   │   ├── Transactable.java (interface)
│   │   │   │   ├── TransactionManager.java (UPDATED with Collections & Streams)
│   │   │   │   └── Transaction.java
│   │   │   ├── ui/                # UI separation
│   │   │   │   ├── AccountUI.java
│   │   │   │   ├── CustomerUI.java
│   │   │   │   └── AccountManagerUI.java
│   │   │   ├── utils/             # Utility classes
│   │   │   │   ├── CustomUtils.java
│   │   │   │   ├── InputValidator.java
│   │   │   │   ├── ValidationUtils.java (NEW: Regex validation)
│   │   │   │   ├── ConcurrencyUtils.java (NEW: Thread utilities)
│   │   │   │   └── FunctionalUtils.java (NEW: Stream utilities)
│   │   │   ├── services/          # NEW: Service layer
│   │   │   │   └── FilePersistenceService.java (NEW: File I/O service)
│   │   │   ├── exceptions/        # Custom exceptions
│   │   │   │   ├── BankException.java
│   │   │   │   ├── ValidationException.java
│   │   │   │   └── InsufficientFundsException.java
│   │   │   └── Main.java          # Entry point
│   │   └── resources/
│   └── test/
│       └── java/                  # Test classes
│           └── AccountTest.java   # Unit tests
├── data/                          # NEW: Data files directory
│   ├── accounts.txt               # Persistent account data
│   └── transactions.txt           # Persistent transaction data
├── docs/                          # Documentation
│   ├── collections-architecture.md
│   └── git-workflow.md
├── README.md
├── pom.xml                        # Maven configuration
└── .gitignore
```

## 🎯 How to Use

### Main Menu Navigation
```
BANK ACCOUNT MANAGEMENT SYSTEM - MAIN MENU
1. Manage Accounts  
2. Perform Transactions  
3. Generate Account Statements  
4. Save/Load Data  
5. Run Concurrent Simulation  
6. Exit  
```

### Key Workflows

#### **Workflow 1: Collections and Functional Migration**
- Data automatically loads from files on startup
- Use Streams for filtering and sorting operations
- Experience improved performance with Collections

#### **Workflow 2: File Persistence Cycle**
1. Start application → data auto-loads from files
2. Perform new transactions
3. Save updates → confirm success
4. Restart → verify data persists correctly

#### **Workflow 3: Regex Validation**
```
Enter customer email: john.smith@bank
✗ Error: Invalid email format. Please enter a valid address (e.g., name@example.com)

Enter customer email: john.smith@bank.com
✓ Email accepted!
```

#### **Workflow 4: Concurrent Transaction Simulation**
```
Running concurrent transaction simulation...
Thread-1: Depositing $500 to ACC001
Thread-2: Depositing $300 to ACC001
Thread-3: Withdrawing $200 from ACC001

Thread-safe operations completed successfully.
Final Balance for ACC001: $6,850.00
```

### Functional Programming Examples

#### **Stream Filtering Example:**
```java
transactions.stream()
    .filter(t -> t.getType().equals("DEPOSIT"))
    .sorted(Comparator.comparing(Transaction::getAmount).reversed())
    .forEach(System.out::println);
```

#### **Stream Aggregation Example:**
```java
double totalDeposits = transactions.stream()
    .filter(t -> t.getType().equals("DEPOSIT"))
    .mapToDouble(Transaction::getAmount)
    .sum();
```

## 🏗️ Modern Java Principles Applied

### **Collections Framework**
- `ArrayList` for ordered transaction storage
- `HashMap<String, Account>` for efficient account lookup
- Type-safe generic collections

### **Functional Programming**
- **Streams API**: For data processing pipelines
- **Lambda Expressions**: Concise functional implementations
- **Method References**: Cleaner code for method calls
- **Functional Interfaces**: `Predicate`, `Consumer`, `Function`

### **File I/O with NIO**
- `Files.lines()` for reading files as streams
- `Paths.get()` for modern path handling
- Try-with-resources for automatic resource management

### **Concurrency**
- `Thread` for concurrent operations
- `synchronized` methods for thread safety
- Parallel streams for data processing

### **Regex Validation**
- `Pattern` and `Matcher` for input validation
- Centralized validation patterns
- User-friendly error messages

## 🧪 Testing the Application

### **Test Scenarios:**
1. **Collections and Functional Migration**
    - Confirm ArrayList and HashMap usage
    - Verify Stream operations work correctly

2. **File Persistence (Round-Trip)**
    - Create accounts and transactions
    - Save data, restart program, verify reload

3. **Regex Validation**
    - Test invalid/valid emails and account numbers
    - Confirm proper error messages

4. **Concurrent Deposit Simulation**
    - Run concurrent simulation
    - Verify synchronized operations and final balance accuracy

5. **Stream-Based Transaction Sorting**
    - Generate transactions, sort by amount/date using Streams
    - Confirm correct ordering

6. **Functional Reduction Operations**
    - Use `reduce()` to calculate total deposits
    - Confirm computed totals match manual calculations

### **Run Tests:**
```bash
# Run all tests
mvn test

# Generate test report
mvn surefire-report:report
```

## 🐛 Troubleshooting

### **Common Issues:**

1. **File Persistence Issues**
   ```bash
   # Check data directory permissions
   ls -la data/
   
   # Verify file paths in code
   ```

2. **Concurrency Issues**
    - Ensure `synchronized` keyword on critical methods
    - Check thread logs for interleaving issues

3. **Stream Processing Errors**
    - Verify terminal operations are called
    - Check for null values in streams

4. **Regex Pattern Issues**
    - Test patterns separately using regex testers
    - Verify pattern strings are correct

### **Data Files Format:**

**accounts.txt:**
```
ACC001,SAVINGS,John Doe,5000.00,ACTIVE
ACC002,CHECKING,Jane Smith,2500.00,ACTIVE
```

**transactions.txt:**
```
TXN001,ACC001,DEPOSIT,1000.00,6000.00,2024-01-15T10:30:00
TXN002,ACC002,WITHDRAWAL,500.00,2000.00,2024-01-15T11:45:00
```

## 📈 Learning Outcomes

This project demonstrates mastery of:

### **Java 21 Features**
- Collections Framework (ArrayList, HashMap)
- Functional Programming (Streams, Lambdas, Method References)
- File I/O with NIO (Files, Paths)
- Concurrency (Threads, synchronized, parallel streams)
- Regex Pattern Matching

### **Software Engineering Principles**
- Data persistence design
- Thread-safe programming
- Input validation and error handling
- Code organization and modularity

### **Development Practices**
- Version control with meaningful commits
- Comprehensive documentation
- Testing strategies
- Clean code principles

## 👥 Contributors

Developed as an educational project demonstrating modern Java programming skills, software design principles, and professional development practices.

## 📄 License

This project is for educational purposes as part of a programming curriculum. All code is available for learning and reference.

---
