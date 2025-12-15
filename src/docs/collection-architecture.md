# Collections Architecture Documentation

## Overview

This document details the migration from array-based data structures to Java Collections Framework (JCF) in the Bank Account Management System. The transition enhances scalability, performance, and maintainability while introducing functional programming paradigms.

## Architecture Evolution

### Before Migration (Week 2)
- **Accounts Storage**: Fixed-size arrays (`Account[]`)
- **Transactions Storage**: Fixed-size arrays (`Transaction[]`)
- **Limitations**:
    - Manual resizing required
    - Inefficient search operations (O(n))
    - No built-in sorting or filtering
    - Memory management overhead

### After Migration (Week 3)
- **Accounts Storage**: `HashMap<String, Account>`
- **Transactions Storage**: `ArrayList<Transaction>`
- **Benefits**:
    - Automatic resizing
    - O(1) account lookup by account number
    - Built-in functional operations via Streams API
    - Better memory management

## Collections Implementation

### 1. Account Management (`AccountManager.java`)

```java
public class AccountManager {
    // Using HashMap for O(1) lookup by account number
    private final Map<String, Account> accounts;
    
    // Using ArrayList for ordered iteration
    private final List<Account> accountList;
    
    public AccountManager() {
        this.accounts = new HashMap<>();
        this.accountList = new ArrayList<>();
    }
    
    // Add account to both collections
    public boolean addAccount(Account account) {
        String accountNumber = account.getAccountNumber();
        
        if (accounts.containsKey(accountNumber)) {
            return false; // Account already exists
        }
        
        accounts.put(accountNumber, account);
        accountList.add(account);
        return true;
    }
    
    // Fast O(1) lookup
    public Account findAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
}
```

### 2. Transaction Management (`TransactionManager.java`)

```java
public class TransactionManager {
    // Using ArrayList for efficient insertion and iteration
    private List<Transaction> transactions;
    
    public TransactionManager() {
        this.transactions = new ArrayList<>(200); // Initial capacity
    }
    
    // Add transaction with automatic resizing
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
    
    // Stream-based filtering for account transactions
    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        return transactions.stream()
            .filter(t -> t.getAccountNumber().equals(accountNumber))
            .collect(Collectors.toList());
    }
}
```

## Functional Programming Integration

### 1. Stream Operations

#### Filtering and Mapping
```java
// Old: Imperative style
List<Transaction> accountTransactions = new ArrayList<>();
for (Transaction transaction : transactions) {
    if (transaction.getAccountNumber().equals(accountNumber)) {
        accountTransactions.add(transaction);
    }
}

// New: Functional style with Streams
List<Transaction> accountTransactions = transactions.stream()
    .filter(t -> t.getAccountNumber().equals(accountNumber))
    .collect(Collectors.toList());
```

#### Aggregation with Reduce
```java
// Calculate total deposits for an account
double totalDeposits = transactions.stream()
    .filter(t -> t.getAccountNumber().equals(accountNumber))
    .filter(t -> t.getType().equals("DEPOSIT"))
    .mapToDouble(Transaction::getAmount)
    .sum();
```

#### Sorting with Comparators
```java
// Sort transactions by timestamp (newest first)
List<Transaction> sortedTransactions = transactions.stream()
    .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
    .collect(Collectors.toList());

// Alternative using method reference
List<Transaction> sortedTransactions = transactions.stream()
    .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
    .collect(Collectors.toList());
```

### 2. Lambda Expressions

```java
// Lambda for filtering
Predicate<Transaction> isDeposit = t -> t.getType().equals("DEPOSIT");

// Lambda for transformation
Function<Transaction, String> toTransactionString = 
    t -> String.format("%s | %s | $%.2f", 
        t.getTransactionId(), 
        t.getType(), 
        t.getAmount());

// Lambda for action
Consumer<Transaction> printTransaction = 
    t -> System.out.println(toTransactionString.apply(t));

// Using all together
transactions.stream()
    .filter(isDeposit)
    .map(toTransactionString)
    .forEach(System.out::println);
```

### 3. Method References

```java
// Static method reference for validation
Predicate<String> isValidEmail = ValidationUtils::isValidEmail;

// Instance method reference
List<Double> balances = accountList.stream()
    .map(Account::getBalance)  // Equivalent to: a -> a.getBalance()
    .collect(Collectors.toList());

// Constructor reference (for File I/O)
List<Transaction> loadedTransactions = Files.lines(Paths.get("transactions.txt"))
    .map(Transaction::new)  // Assuming constructor takes String
    .collect(Collectors.toList());
```

## Performance Analysis

### Time Complexity Comparison

| Operation | Arrays (Old) | Collections (New) | Improvement |
|-----------|--------------|-------------------|-------------|
| Account Lookup | O(n) | O(1) | 100x faster |
| Add Account | O(1) amortized | O(1) amortized | Similar |
| Find Transactions | O(n) | O(n) with Streams | Better API |
| Sort Transactions | O(n log n) manual | O(n log n) built-in | Cleaner code |
| Filter Transactions | O(n) manual | O(n) with Streams | More expressive |

### Memory Usage

| Structure | Arrays | Collections | Notes |
|-----------|---------|-------------|-------|
| Fixed Size | Yes | No | Collections auto-resize |
| Overhead | Low | Medium | HashMap has some overhead |
| Cache Locality | Good | Variable | Arrays better for iteration |
| Flexibility | Low | High | Collections offer more operations |

## Thread Safety Considerations

### Concurrent Modifications

```java
// Using synchronized collections for thread safety
private List<Transaction> transactions = 
    Collections.synchronizedList(new ArrayList<>());

// Or using concurrent collections
private Map<String, Account> accounts = 
    new ConcurrentHashMap<>();

// Synchronized methods for critical sections
public synchronized void addTransaction(Transaction transaction) {
    transactions.add(transaction);
}
```

### Parallel Streams

```java
// Parallel processing for large datasets
double totalBalance = accountList.parallelStream()
    .mapToDouble(Account::getBalance)
    .sum();

// Note: Ensure thread-safety when using parallel streams
List<Transaction> safeTransactions = 
    Collections.synchronizedList(new ArrayList<>());
```

## Design Patterns with Collections

### 1. Repository Pattern

```java
public class AccountRepository {
    private Map<String, Account> accountStore = new HashMap<>();
    
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accountStore.get(id));
    }
    
    public List<Account> findAll() {
        return new ArrayList<>(accountStore.values());
    }
    
    public List<Account> findByType(String type) {
        return accountStore.values().stream()
            .filter(a -> a.getAccountType().equals(type))
            .collect(Collectors.toList());
    }
}
```

### 2. Strategy Pattern with Lambdas

```java
public class TransactionFilter {
    private Predicate<Transaction> filterStrategy;
    
    public TransactionFilter(Predicate<Transaction> filterStrategy) {
        this.filterStrategy = filterStrategy;
    }
    
    public List<Transaction> filter(List<Transaction> transactions) {
        return transactions.stream()
            .filter(filterStrategy)
            .collect(Collectors.toList());
    }
}

// Usage
TransactionFilter depositFilter = new TransactionFilter(
    t -> t.getType().equals("DEPOSIT")
);

TransactionFilter largeTransactionFilter = new TransactionFilter(
    t -> t.getAmount() > 1000.00
);
```

## Migration Steps

### Phase 1: Data Structure Replacement
1. Replace `Account[]` with `HashMap<String, Account>`
2. Replace `Transaction[]` with `ArrayList<Transaction>`
3. Update constructors and initializations
4. Modify getter methods to return List instead of array

### Phase 2: Loop Conversion to Streams
1. Identify for-loops for filtering/sorting/aggregation
2. Replace with appropriate Stream operations
3. Test each conversion individually
4. Ensure exception handling remains intact

### Phase 3: API Updates
1. Update method signatures to use Collections
2. Add new methods leveraging Streams API
3. Deprecate old array-based methods
4. Update UI layer to use new APIs

### Phase 4: Performance Optimization
1. Add initial capacities where known
2. Use parallel streams for large datasets
3. Implement caching for frequent operations
4. Add lazy loading where appropriate

## Best Practices

### 1. Collection Initialization
```java
// Good: Specify initial capacity when known
List<Transaction> transactions = new ArrayList<>(1000);
Map<String, Account> accounts = new HashMap<>(50);

// Good: Use diamond operator
List<Account> accountList = new ArrayList<>();

// Avoid: Raw types
List badList = new ArrayList(); // Raw type - don't use
```

### 2. Immutable Collections
```java
// Return defensive copies
public List<Transaction> getAllTransactions() {
    return new ArrayList<>(transactions); // Defensive copy
}

// Use Collections.unmodifiableList for read-only views
public List<Transaction> getTransactionView() {
    return Collections.unmodifiableList(transactions);
}
```

### 3. Stream Best Practices
```java
// Chain operations properly
transactions.stream()
    .filter(t -> t.getType().equals("DEPOSIT"))
    .sorted(Comparator.comparing(Transaction::getAmount).reversed())
    .limit(10)
    .forEach(System.out::println);

// Avoid side effects in intermediate operations
// BAD:
transactions.stream()
    .filter(t -> {
        System.out.println(t); // Side effect!
        return t.getAmount() > 1000;
    });
    
// GOOD:
transactions.stream()
    .filter(t -> t.getAmount() > 1000)
    .forEach(System.out::println); // Terminal operation
```

### 4. Error Handling with Streams
```java
// Handle null values safely
List<String> accountNumbers = transactions.stream()
    .map(Transaction::getAccountNumber)
    .filter(Objects::nonNull) // Filter out nulls
    .collect(Collectors.toList());

// Or use Optional
List<String> safeAccountNumbers = transactions.stream()
    .map(Transaction::getAccountNumber)
    .filter(Optional::isPresent)
    .map(Optional::get)
    .collect(Collectors.toList());
```

## Testing Strategy

### Unit Tests for Collections
```java
@Test
void testAccountLookupPerformance() {
    AccountManager manager = new AccountManager();
    // Add 1000 accounts
    for (int i = 0; i < 1000; i++) {
        manager.addAccount(createTestAccount(i));
    }
    
    // Test O(1) lookup
    long startTime = System.nanoTime();
    Account account = manager.findAccount("ACC500");
    long endTime = System.nanoTime();
    
    assertNotNull(account);
    assertTrue((endTime - startTime) < 1000000); // Should be < 1ms
}

@Test
void testStreamOperations() {
    TransactionManager manager = new TransactionManager();
    // Add test transactions
    addTestTransactions(manager);
    
    // Test filtering with streams
    List<Transaction> deposits = manager.getAllTransactions().stream()
        .filter(t -> t.getType().equals("DEPOSIT"))
        .collect(Collectors.toList());
    
    assertEquals(expectedDepositCount, deposits.size());
}
```

### Integration Tests
```java
@Test
void testEndToEndWithCollections() {
    // Test complete workflow with Collections
    AccountManager accountManager = new AccountManager();
    TransactionManager transactionManager = new TransactionManager();
    
    // Create accounts
    Account account = new SavingsAccount("ACC001", customer, 1000.00);
    accountManager.addAccount(account);
    
    // Process transactions
    Transaction deposit = new Transaction("TXN001", "ACC001", "DEPOSIT", 500.00);
    transactionManager.addTransaction(deposit);
    
    // Verify using Streams
    double totalDeposits = transactionManager.getAllTransactions().stream()
        .filter(t -> t.getAccountNumber().equals("ACC001"))
        .filter(t -> t.getType().equals("DEPOSIT"))
        .mapToDouble(Transaction::getAmount)
        .sum();
    
    assertEquals(500.00, totalDeposits, 0.01);
}
```

## Future Enhancements

### 1. Database Integration
- Replace in-memory Collections with JPA entities
- Use Spring Data repositories
- Implement caching layer

### 2. Advanced Stream Operations
- Implement custom collectors
- Use `flatMap` for complex transformations
- Implement reactive streams with Project Reactor

### 3. Performance Monitoring
- Add metrics for Collection operations
- Monitor memory usage
- Implement profiling for Stream operations

### 4. Advanced Data Structures
- Consider `ConcurrentSkipListMap` for sorted concurrent access
- Use `LinkedHashMap` for maintaining insertion order
- Implement `WeakHashMap` for cache-like behavior

## Conclusion

The migration to Java Collections Framework and functional programming has significantly improved the Bank Account Management System by:

1. **Improved Performance**: O(1) account lookups vs O(n) with arrays
2. **Better Scalability**: Automatic resizing and memory management
3. **Enhanced Readability**: Declarative code with Streams and Lambdas
4. **Increased Maintainability**: Standard APIs and fewer bugs
5. **Future-Proofing**: Ready for database and concurrency enhancements

The architecture now follows modern Java best practices and provides a solid foundation for future enhancements while maintaining backward compatibility with existing features.

---
