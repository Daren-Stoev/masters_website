package com.example.application.data.entity;

import org.semanticweb.owlapi.model.IRI;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
public class Customer {
    private String username;
    private String email;
    private String password;
    private byte[] password_salt;
    private String subscriptionType;
    private String firstName;
    private String lastName;


    public Customer(String username,String password, String email,String subscriptionType,String firstName,String lastName) {
        this.username = username;
        this.email = email;
        setPassword(password);
        this.subscriptionType = subscriptionType;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public Customer(String username,String password,String passwordSalt, String email,String subscriptionType,String firstName,String lastName) {
        this.username = username;
        this.email = email;
        setPassword_saltFromString(passwordSalt);
        this.password = password;
        this.subscriptionType = subscriptionType;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public Customer(){}


    // Getters and setters for the attributes

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        // Generate a random salt value
        if(this.password_salt == null) {
            this.password_salt = generateSalt();
        }

        // Compute the hash of the password using the salt
        byte[] passwordHash = hashPassword(password, this.password_salt);

        // Store the hashed password in the customer object
        this.password = bytesToHex(passwordHash);
    }

    public byte[] getPassword_salt() {
        return password_salt;
    }

    public String getPassword_saltAsString() {
        return bytesToHex(password_salt);
    }

    public void setPassword_salt(byte[] password_salt) {
        this.password_salt = password_salt;
    }

    public void setPassword_saltFromString(String password_salt){
        this.password_salt = hexToBytes(password_salt);
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    // Other methods and behaviors of the Customer class can be added here

    public IRI getIndividualIRI(String ontologyIRIStr) {
        // Generate and return the individual IRI based on a unique identifier
        return IRI.create(ontologyIRIStr + getEmail());
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private byte[] hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] passwordBytes = password.getBytes();
            return md.digest(passwordBytes);
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception
        }
        return null;
    }

    public boolean verifyPassword(String passwordToCheck) {

        byte[] hashedPasswordToCheck = hashPassword(passwordToCheck, this.password_salt);
        byte[] storedPasswordBytes = hexToBytes(this.password);

        return constantTimeComparison(hashedPasswordToCheck, storedPasswordBytes);
    }

    private static boolean constantTimeComparison(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public void printInfo()
    {
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Password: " + getPassword());
        System.out.println("Subscription Type: " + getSubscriptionType());
        System.out.println("First Name: " + getFirstName());
        System.out.println("Last Name: " + getLastName());
    }

}

