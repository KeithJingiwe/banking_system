package model;

import java.time.LocalDate;

public class IndividualCustomer extends Customer {
    private String firstName;
    private String surname;
    private final String omangNumber;
    private final LocalDate dateOfBirth;
    private boolean employed;

    public IndividualCustomer(String userName, String plainPassword, String firstName, String surname,
                              String omangNumber, LocalDate dateOfBirth, boolean employed,
                              String address, String phone, String email) {
        super(userName, plainPassword, address, phone, email);
        this.firstName = firstName;
        this.surname = surname;
        this.omangNumber = omangNumber;
        this.dateOfBirth = dateOfBirth;
        this.employed = employed;
    }

    @Override
    public String getDisplayName() { return firstName + " " + surname; }

    public boolean isEmployed() { return employed; }
    public void setEmployed(boolean employed) { this.employed = employed; }

    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getOmangNumber() { return omangNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setSurname(String surname) { this.surname = surname; }
}