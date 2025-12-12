# Collections Architecture Documentation

## Overview

This document describes the transition from array-based data structures to Java Collections Framework (JCF) in the Bank Account Management System. This architectural change enhances scalability, maintainability, and performance while maintaining backward compatibility.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Main Application                         │
│                                                             │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │ AccountUI    │    │ CustomerUI   │    │ Main Class   │  │
│  │ (UI Layer)   │    │ (UI Layer)   │    │ (Driver)     │  │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘  │
│         │                   │                   │          │
└─────────┼───────────────────┼───────────────────┼──────────┘
          │                   │                   │
          │                   │                   │
┌─────────▼───────────────────▼───────────────────▼──────────┐
│              Business Logic Layer (Managers)               │
│                                                            │
│  ┌──────────────────┐      ┌──────────────────┐          │
│  │ AccountManager   │      │ CustomerManager  │          │
│  │ • HashMap        │      │ • Uses           │          │
│  │   <String,       │◄─────┤   AccountManager │          │
│  │    Account>      │      │   for data       │          │
│  │ • ArrayList      │      └──────────────────┘          │
│  │   <Account>      │                                    │
│  └────────┬─────────┘                                    │
│           │                                              │
│  ┌────────▼─────────┐                                    │
│  │ TransactionManager│                                    │
│  │ • ArrayList      │                                    │
│  │   <Transaction>  │                                    │
│  └──────────────────┘                                    │
│                                                          │
└──────────────────────────────────────────────────────────┘
          │                   │                   │
          │                   │                   │
┌─────────▼───────────────────▼───────────────────▼──────────┐
│              Domain Model Layer (Entities)                 │
│                                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │ Account      │  │ Customer     │  │ Transaction  │    │
│  │ • Savings    │  │ • Regular    │  │ • DEPOSIT    │    │
│  │ • Checking   │  │ • Premium    │  │ • WITHDRAWAL │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

## Data Structure Changes

### Before (Array-based)
```java
// AccountManager
private Account[] accounts;
private int accountCount;

// TransactionManager  
private Transaction[] transactions;
private int transactionCount;
```

### After (Collections-based)
```java
// AccountManager
private Map<String, Account> accounts;        // Fast lookup by account number
private List<Account> accountList;            // Maintains insertion order

// TransactionManager
private List<Transaction> transactions;       // Dynamic resizing
```

## Key Design Decisions

### 1. **AccountManager: Hybrid Approach (HashMap + ArrayList)**

**Why both HashMap and ArrayList?**
- **HashMap<String, Account>**: O(1) lookup by account number
- **ArrayList<Account>**: Maintains insertion order for display purposes
- **Dual storage ensures both fast access and ordered iteration**

```java
public class AccountManager {
    private Map<String, Account> accounts;     // Key: accountNumber
    private List<Account> accountList;         // Maintains order
    
    public boolean addAccount(Account account) {
        // Add to both structures
        accounts.put(account.getAccountNumber(), account);
        accountList.add(account);
        return true;
    }
    
    public Account findAccount(String accountNumber) {
        return accounts.get(accountNumber);    // O(1) lookup
    }
}
```

### 2. **TransactionManager: Simple ArrayList**

**Why ArrayList?**
- Transactions need sequential access (by timestamp)
- Natural ordering by insertion time
- Easy sorting with `Collections.sort()`
- No need for key-based lookup

```java
public class TransactionManager {
    private List<Transaction> transactions;
    
    public void addTransaction(Transaction t) {
        transactions.add(t);  // Always succeeds, no capacity limits
    }
    
    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        return transactions.stream()
            .filter(t -> t.getAccountNumber().equals(accountNumber))
            .collect(Collectors.toList());
    }
}
```

### 3. **CustomerManager: Uses AccountManager's Public API**

**Why not store its own collection?**
- Follows Single Responsibility Principle
- Avoids data duplication
- Uses AccountManager as the single source of truth
- Reduces synchronization issues

```java
public class CustomerManager {
    private AccountManager accountManager;
    
    public List<Customer> getAllCustomers() {
        return accountManager.getAccounts().stream()
            .map(Account::getCustomer)
            .distinct()
            .collect(Collectors.toList());
    }
}
```

## Performance Characteristics

| Operation | Array-based | Collections-based | Improvement |
|-----------|------------|-------------------|-------------|
| Find Account by ID | O(n) | O(1) | **Significant** |
| Add Account | O(1) (if space) | O(1)* | Similar |
| Get All Accounts | O(n) | O(n) | Same |
| Transaction Lookup | O(n) | O(n) | Same |
| Memory Usage | Fixed | Dynamic | **More efficient** |
| Capacity Limits | Fixed (50/200) | Unlimited | **Major improvement** |

*Amortized constant time for ArrayList expansion

## Memory Management

### Array Approach
```java
// Wasted space when not full
Account[] accounts = new Account[50];  // Always allocates memory for 50
```

### Collections Approach
```java
// Grows as needed, no wasted space
List<Account> accounts = new ArrayList<>();  // Starts empty, grows dynamically
Map<String, Account> map = new HashMap<>();  // Efficient hash-based storage
```

## Thread Safety Considerations

### Current Implementation
- **Not thread-safe** - Designed for single-threaded console application
- Simple `ArrayList` and `HashMap` (non-synchronized)
- Appropriate for current use case

### Future Scalability Options
```java
// If multi-threading needed:
private List<Account> accountList = Collections.synchronizedList(new ArrayList<>());
private Map<String, Account> accounts = Collections.synchronizedMap(new HashMap<>());

// Or use concurrent collections:
private ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();
private CopyOnWriteArrayList<Account> accountList = new CopyOnWriteArrayList<>();
```

## API Compatibility Layer

To maintain backward compatibility during transition:

```java
public class AccountManager {
    // New collections-based storage
    private Map<String, Account> accounts;
    private List<Account> accountList;
    
    // Backward compatible methods
    public Account[] getAccounts() {
        return accountList.toArray(new Account[0]);
    }
    
    public int getActualAccountCount() {
        return accountList.size();
    }
}
```

## Testing Strategy

### Unit Tests Updated
1. **AccountManagerTest**: Updated to test List instead of array
2. **CustomerManagerTest**: No changes needed (uses public API)
3. **TransactionManagerTest**: Updated to test List operations

### Key Test Scenarios
```java
// Test HashMap uniqueness
@Test
void duplicateAccountNumbersNotAllowed() {
    Account account1 = new Account("ACC001", ...);
    Account account2 = new Account("ACC001", ...);  // Same number
    accountManager.addAccount(account1);
    assertFalse(accountManager.addAccount(account2));  // Should fail
}

// Test ArrayList ordering
@Test  
void accountsMaintainInsertionOrder() {
    accountManager.addAccount(account1);
    accountManager.addAccount(account2);
    accountManager.addAccount(account3);
    
    List<Account> all = accountManager.getAccounts();
    assertEquals(account1, all.get(0));  // First added
    assertEquals(account2, all.get(1));  // Second added
}
```

## Migration Benefits

### 1. **Code Quality Improvements**
- **Cleaner code**: No manual array bounds checking
- **Type safety**: Compile-time type checking with generics
- **Readability**: Standard Java Collections API

### 2. **Performance Improvements**
- **Account lookup**: O(1) vs O(n)
- **Dynamic resizing**: No fixed capacity limits
- **Memory efficiency**: No pre-allocated empty slots

### 3. **Maintainability**
- **Encapsulation**: Implementation hidden behind interface
- **Flexibility**: Easy to switch implementations (e.g., HashMap → TreeMap)
- **Testability**: Mock collections easily in tests

### 4. **Scalability**
- **Horizontal scaling**: Can handle unlimited accounts/transactions
- **Feature additions**: Easy to add new query methods
- **Integration ready**: Standard collections work with streams, sorting, etc.

## Future Enhancements

### 1. **Advanced Collections**
```java
// Could switch to specialized collections:
private NavigableMap<String, Account> accounts = new TreeMap<>();  // Sorted accounts
private LinkedHashMap<String, Account> accounts = new LinkedHashMap<>();  // LRU cache
```

### 2. **Stream API Integration**
```java
public List<Account> getHighValueAccounts(double threshold) {
    return accountList.stream()
        .filter(a -> a.getBalance() > threshold)
        .sorted(Comparator.comparing(Account::getBalance).reversed())
        .collect(Collectors.toList());
}
```

### 3. **Persistence Integration**
```java
// Easy serialization with collections
public void saveToFile(String filename) throws IOException {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
        oos.writeObject(new ArrayList<>(accountList));  // Serialize list
    }
}
```

## Conclusion

The transition to Java Collections Framework provides:
- **✅ Better performance** for key operations
- **✅ Improved memory efficiency**
- **✅ Cleaner, more maintainable code**
- **✅ Built-in functionality** (sorting, filtering, etc.)
- **✅ Future-proof architecture** for enhancements
- **✅ Maintained backward compatibility**

