package com.example.myapplication.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Contact implements Serializable {
    
    private static final long serialVersionUID = 2139670243307764033L;

    private long id;
    private String name;
    private List<String> emails = new ArrayList<>();
    private List<String> phoneNumbers = new ArrayList<>();

    public void addEmailAddress(String email) {
        this.emails.add(email);
    }

    public void addPhoneNumber(String phone) {
        this.phoneNumbers.add(phone);
    }

    public List<String> getEmails() {
        return this.emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            return this.getId() == ((Contact) other).getId();
        }
        return false;
    }
}