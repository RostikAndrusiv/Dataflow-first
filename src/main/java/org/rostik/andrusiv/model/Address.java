package org.rostik.andrusiv.model;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable {

    private String addressLine;
    private int zipCode;

    public Address(String addressLine, int zipCode) {
        this.addressLine = addressLine;
        this.zipCode = zipCode;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public Address() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return zipCode == address.zipCode && Objects.equals(addressLine, address.addressLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressLine, zipCode);
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressLine='" + addressLine + '\'' +
                ", zipCode=" + zipCode +
                '}';
    }
}
