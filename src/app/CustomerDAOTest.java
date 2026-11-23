
package app;

import dao.CustomerDAO;
import dao.Schema;
import model.CompanyCustomer;
import model.Customer;
import model.IndividualCustomer;

import java.sql.SQLException;
import java.time.LocalDate;

public class CustomerDAOTest {

    public static void main(String[] args) throws SQLException {

        Schema.create();

        CustomerDAO dao = new CustomerDAO();

        // Synchronize IDs from existing customers.
        // Note: this does not cover users without customer records.
        dao.findAll();

        // Save individual customer
        if (dao.findByUsername("thabo") == null) {

            IndividualCustomer thabo = new IndividualCustomer(
                    "thabo",
                    "Pass#1234",
                    "Thabo",
                    "Molefe",
                    "128766789",
                    LocalDate.of(1998, 4, 12),
                    true,
                    "Plot 123, Palapye",
                    "71234567",
                    "thabo@mail.com"
            );

            dao.save(thabo);
            System.out.println("Thabo saved successfully.");

        } else {
            System.out.println("Thabo already exists.");
        }

        // Save company customer
        if (dao.findByUsername("acme") == null) {

            CompanyCustomer acme = new CompanyCustomer(
                    "acme",
                    "Pass#5678",
                    "Acme Traders",
                    "BW0001234",
                    "Neo Kgosi",
                    "Gaborone",
                    "3951234",
                    "info@acme.co.bw"
            );

            dao.save(acme);
            System.out.println("Acme saved successfully.");

        } else {
            System.out.println("Acme already exists.");
        }

        // Read customers
        Customer t = dao.findByUsername("thabo");
        Customer a = dao.findByUsername("acme");

        System.out.println("Loaded: " + t);
        System.out.println("Loaded: " + a);

        // Test authentication
        if (t != null) {
            System.out.println(
                    "Login successful: " +
                            t.authenticate("Pass#1234")
            );

            // Update customer details
            t.updateDetails(
                    "Plot 9, Gaborone",
                    "72000000",
                    "thabo@new.com"
            );

            ((IndividualCustomer) t).setEmployed(false);

            dao.update(t);

            IndividualCustomer reloaded =
                    (IndividualCustomer) dao.findById(
                            t.getCustomerId()
                    );

            System.out.println(
                    "After update: " +
                            reloaded.getAddress() +
                            " | employed=" +
                            reloaded.isEmployed()
            );
        }

        // Test duplicate username
        try {

            dao.save(new IndividualCustomer(
                    "thabo",
                    "Other#999",
                    "Copy",
                    "Cat",
                    "000000000",
                    LocalDate.of(2000, 1, 1),
                    false,
                    "Nowhere",
                    "0",
                    "x@x.com"
            ));

        } catch (SQLException e) {

            System.out.println(
                    "Duplicate insertion refused. " +
                            "Customers stored: " +
                            dao.findAll().size()
            );
        }
    }
}
