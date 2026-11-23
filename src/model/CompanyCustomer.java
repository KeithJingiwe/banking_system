package model;

public class CompanyCustomer extends Customer{
    private final String companyName;
    private final String registrationNumber;
    private String contactPerson;

    public CompanyCustomer(String username, String password, String companyName, String registrationNumber, String contactPerson, String address, String phone, String email) {
        super(username, password, address, phone, email);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.contactPerson = contactPerson;

    }

    public CompanyCustomer(String customerId, String userID, String userName, String passwordHash,
                           String companyName, String registrationNumber, String contactPerson,
                           String address, String phone, String email,
                           int failedAttempts, boolean locked, boolean active) {
        super(customerId, userID, userName, passwordHash, address, phone, email,
                failedAttempts, locked, active);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.contactPerson = contactPerson;
    }

    @Override
    public String getDisplayName() {return companyName;}
    public String getCompanyName() {return companyName;}
    public String getRegistrationNumber() {return registrationNumber;}
    public String getContactPerson() {return contactPerson;}
    public void setContactPerson(String contactPerson) {this.contactPerson = contactPerson;}
}
