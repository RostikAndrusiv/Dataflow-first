package org.rostik.andrusiv.model;

import com.google.gson.annotations.SerializedName;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PersonMongo implements Serializable {
    @SerializedName("_id")
    private String id;
    private String name;
    private int age;
    private boolean isActive;
    private double balance;
    private List<String> phoneNumbers;
    @BsonProperty("$date")
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dob;

    private Address address;

    public PersonMongo() {
    }

    public PersonMongo(String id, String name, int age, boolean isActive, double balance, List<String> phoneNumbers, LocalDateTime dob, Address address) {
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

    public LocalDateTime getDob() {
        return dob;
    }

    public void setDob(LocalDateTime dob) {
        this.dob = dob;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonMongo that = (PersonMongo) o;
        return age == that.age && isActive == that.isActive && Double.compare(that.balance, balance) == 0 && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(phoneNumbers, that.phoneNumbers) && Objects.equals(dob, that.dob) && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, isActive, balance, phoneNumbers, dob, address);
    }

    @Override
    public String toString() {
        return "PersonMongo{" +
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
