package com.example.mobilepayroll;

public class UserModel {

    public UserModel() {
    }

    String fullName, department, imageUrl, email, phoneNumber, status, basicPay;

    public String getBasicPay() {
        return basicPay;
    }

    public void setBasicPay(String basicPay) {
        this.basicPay = basicPay;
    }

    public UserModel(String fullName, String department, String imageUrl, String email, String phoneNumber, String status, String basicPay) {
        this.fullName = fullName;
        this.department = department;
        this.imageUrl = imageUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.basicPay = basicPay;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
