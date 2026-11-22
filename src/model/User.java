package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class User {
    public static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int ITERATIONS = 65_536;

    private final String userID;
    private final String userName;
    private final String passwordHash;
    private final Role role;
    private int failedAttempts;
    private boolean locked;
    private boolean active = true;
    private String address;
    private String phone;
    private String email;

    public User(String userName, String plainPassword, Role role, String address, String phone, String email) {
        this.userID = IdGenerator.next("USR");
        this.userName = userName;
        this.passwordHash = hashPassword(plainPassword);
        this.role = role;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public User(String userName, String plainPasswordHash, Role role) {
        this(userName,plainPasswordHash,role,null,null,null);
    }
    public boolean authenticate(String password) {
        if (!active || locked || password == null) return false;
        if (matches(password, passwordHash)){
            resetFailedAttempts();
            return true;
        }

        failedAttempts++;
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) locked = true;
        return false;
    }

    public void updateDetails(String address, String phone, String email) {
       this.address = address;
       this.phone = phone;
       this.email = email;
    }

    public boolean isLocked() {return locked;}
    public void resetFailedAttempts() {failedAttempts = 0; locked = false;}
    public void deactivate() {active = false;}

    private static String hashPassword(String plain) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(derive(plain,salt));
    }

    private static boolean matches(String plain, String stored) {
        String [] parts = stored.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expected = Base64.getDecoder().decode(parts[1]);
        return MessageDigest.isEqual(expected, derive(plain, salt));
    }

    private static byte[] derive(String plain, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(plain.toCharArray(), salt, ITERATIONS, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }
public String getUserID() {return userID;}
    public String getUserName() {return userName;}
    public String getPasswordHash() {return passwordHash;}
    public Role getRole() {return role;}
    public int getFailedAttempts() {return failedAttempts;}
    public boolean isActive() {return active;}
    public String getAddress() {return address;}
    public String getPhone() {return phone;}
    public String getEmail() {return email;}

    @Override
    public String toString() {return userName + " (" + role + ")";}
}
