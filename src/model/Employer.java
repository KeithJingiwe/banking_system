package model;

public class Employer {
    private final String employerName;
    private final String employerAddress;
    private final String employeeNumber;

    public  Employer(String employerName, String employerAddress, String employeeNumber) {
        if (isBlank(employerName) || isBlank (employerAddress)){
            throw new IllegalArgumentException("EmployerName or EmployerAddress is required.");
        }
        this.employerName = employerName;
        this.employerAddress = employerAddress;
        this.employeeNumber = employeeNumber;
    }
    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public String getEmployerName() {return employerName;}
    public String getEmployerAddress() {
        return employerAddress;
    }
    public String getEmployeeNumber() {return employeeNumber;}

    @Override
    public String toString() {return employerName + ", " + employerAddress;}
}
