package com.example.firebaseauthexample;

public class User {

    private String first;
    private String last;
    private int born;
    private Address address;

    public User(String first, String last, int born, Address address) {
        this.first = first;
        this.last = last;
        this.born = born;
        this.address = address;
    }

    public User() {

    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public int getBorn() {
        return born;
    }

    public void setBorn(int born) {
        this.born = born;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "first='" + first + '\'' +
                ", last='" + last + '\'' +
                ", born=" + born +
                ", address=" + address +
                '}';
    }
}
