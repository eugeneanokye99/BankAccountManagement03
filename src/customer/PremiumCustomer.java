package customer;

import utils.CustomUtils;

public class PremiumCustomer extends Customer {
    private final double minimumBalance;

    public PremiumCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
        this.minimumBalance = 10000.0; // $10,000 minimum for premium status
    }


    @Override
    public void displayCustomerDetails() {
        CustomUtils.print("=== Premium Customer Details ===");
        CustomUtils.print("Customer ID: " + getCustomerId());
        CustomUtils.print("Name: " + getName());
        CustomUtils.print("Age: " + getAge());
        CustomUtils.print("Contact: " + getContact());
        CustomUtils.print("Address: " + getAddress());
        CustomUtils.print("Type: Premium Customer");
        CustomUtils.print("Minimum Balance Required: $" + String.format("%.2f", minimumBalance));
        CustomUtils.print("Benefits: No monthly fees, Priority service");
    }

    @Override
    public String getCustomerType() {
        return "Premium";
    }
}