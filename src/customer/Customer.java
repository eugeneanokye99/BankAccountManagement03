package customer;

public abstract class Customer {
    private final String customerId;
    private final String name;
    private final int age;
    private final String contact;
    private final String address;

    private static int customerCounter = 0;

    public Customer(String name, int age, String contact, String address) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.address = address;
    }

    private String generateCustomerId() {
        customerCounter++;
        return String.format("CUS%03d", customerCounter);
    }

    // Getters
    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }


    // Abstract methods
    public abstract void displayCustomerDetails();
    public abstract String getCustomerType();


}