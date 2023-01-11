package org.rostik.andrusiv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PersonFirestore implements Serializable {
    private String id;
    private String name;
    private int age;
    private boolean isActive;
    private double balance;
    private List<String> phoneNumbers;
    private Date dob;
    private Address address;

    public PersonFirestore() {
    }

    public PersonFirestore(String id, String name, int age, boolean isActive, double balance, List<String> phoneNumbers, Date dob, Address address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.isActive = isActive;
        this.balance = balance;
        this.phoneNumbers = phoneNumbers;
        this.dob = dob;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PersonFirestore{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", isActive=" + isActive +
                ", balance=" + balance +
                ", phoneNumbers=" + phoneNumbers +
                ", dob=" + dob +
                ", address=" + address +
                '}';
    }
}
