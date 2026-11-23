package app;

import dao.Schema;
import dao.UserDAO;
import model.Role;
import model.User;

import java.sql.SQLException;

public class UserDAOTest {
    public static void main(String[] args) throws SQLException {
        Schema.create();
        UserDAO dao = new UserDAO();

        User teller = dao.findByUsername("teller1");
        if (teller == null) {
            dao.save(new User("teller1", "Teller#123", Role.TELLER));
            System.out.println("Created teller1");
            teller = dao.findByUsername("teller1");
        } else {
            System.out.println("teller1 already stored");
        }

        System.out.println("Loaded: " + teller + " id=" + teller.getUserID());
        System.out.println("Correct password: " + teller.authenticate("Teller#123"));

        teller.authenticate("a");
        teller.authenticate("b");
        teller.authenticate("c");
        dao.update(teller);
        System.out.println("Locked after 3 wrong (from DB): " + dao.findByUsername("teller1").isLocked());

        teller.resetFailedAttempts();
        dao.update(teller);
        System.out.println("Locked after reset (from DB): " + dao.findByUsername("teller1").isLocked());

        User visitor = new User("visitor", "Visit#123", Role.CUSTOMER);
        dao.save(visitor);
        System.out.println("Visitor saved as " + visitor.getUserID());
        dao.delete(visitor.getUserID());
        System.out.println("Visitor still exists: " + (dao.findByUsername("visitor") != null));
    }
}