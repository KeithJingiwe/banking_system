package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import model.IdGenerator;
import java.sql.ResultSet;

public class Schema {
    private Schema() { }

    private static void syncIds() throws SQLException {
        String[][] sources = {
                {"users", "user_id"},
                {"customers", "customer_id"},
                {"accounts", "account_number"},
                {"transactions", "transaction_id"},
                {"interest_payments", "payment_id"}};
        try (Connection c = Database.connect(); Statement s = c.createStatement()) {
            for (String[] src : sources) {
                try (ResultSet rs = s.executeQuery("SELECT " + src[1] + " FROM " + src[0])) {
                    while (rs.next()) {
                        IdGenerator.syncFrom(rs.getString(1));
                    }
                }
            }
        }
    }

    public static void create() throws SQLException {
        try (Connection c = Database.connect(); Statement s =c.createStatement()) {

            s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id         VARCHAR(20) PRIMARY KEY,
                    username        VARCHAR(50) NOT NULL UNIQUE,
                    password_hash   VARCHAR(255) NOT NULL,
                    user_role       VARCHAR(20) NOT NULL,
                    address         VARCHAR(200),
                    phone           VARCHAR(30),
                    email           VARCHAR(100),
                    failed_attempts INT NOT NULL DEFAULT 0,
                    locked          BOOLEAN NOT NULL DEFAULT FALSE,
                    active          BOOLEAN NOT NULL DEFAULT TRUE
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id         VARCHAR(20) PRIMARY KEY,
                    user_id             VARCHAR(20) NOT NULL UNIQUE,
                    customer_type       VARCHAR(20) NOT NULL,
                    first_name          VARCHAR(50),
                    surname             VARCHAR(50),
                    omang_number        VARCHAR(20),
                    date_of_birth       DATE,
                    employed            BOOLEAN,
                    company_name        VARCHAR(100),
                    registration_number VARCHAR(50),
                    contact_person      VARCHAR(100),
                    FOREIGN KEY (user_id) REFERENCES users(user_id)
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS employers (
                    employer_id      INT AUTO_INCREMENT PRIMARY KEY,
                    employer_name    VARCHAR(100) NOT NULL,
                    employer_address VARCHAR(200) NOT NULL,
                    employee_number  VARCHAR(30)
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    account_number VARCHAR(20) PRIMARY KEY,
                    account_type   VARCHAR(20) NOT NULL,
                    branch         VARCHAR(50),
                    date_opened    DATE NOT NULL,
                    status         VARCHAR(10) NOT NULL,
                    balance        DECIMAL(15,2) NOT NULL,
                    customer_id    VARCHAR(20) NOT NULL,
                    employer_id    INT,
                    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
                    FOREIGN KEY (employer_id) REFERENCES employers(employer_id)
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id VARCHAR(20) PRIMARY KEY,
                    account_number VARCHAR(20) NOT NULL,
                    txn_type       VARCHAR(20) NOT NULL,
                    amount         DECIMAL(15,2) NOT NULL,
                    txn_time       TIMESTAMP NOT NULL,
                    balance_after  DECIMAL(15,2) NOT NULL,
                    description    VARCHAR(200),
                    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS interest_payments (
                    payment_id         VARCHAR(20) PRIMARY KEY,
                    run_date           DATE,
                    status             VARCHAR(20) NOT NULL,
                    accounts_processed INT NOT NULL,
                    total_paid         DECIMAL(15,2) NOT NULL,
                    failure_reason     VARCHAR(200)
                )""");
        }

        syncIds();
    }
}
