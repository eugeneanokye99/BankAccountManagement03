package customer;

import utils.CustomUtils;

public class RegularCustomer extends Customer {

    public RegularCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
    }

    @Override
    public void displayCustomerDetails() {
        CustomUtils.print("=== Regular Customer Details ===");
        CustomUtils.print("Customer ID: " + getCustomerId());
        CustomUtils.print("Name: " + getName());
        CustomUtils.print("Age: " + getAge());
        CustomUtils.print("Contact: " + getContact());
        CustomUtils.print("Address: " + getAddress());
        CustomUtils.print("Type: Regular Customer");
        CustomUtils.print("Benefits: Standard banking services");
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }
}