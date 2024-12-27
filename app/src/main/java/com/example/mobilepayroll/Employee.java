package com.example.mobilepayroll;

public class Employee {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String department;
    private String basicPay;
    private String imageUrl;
    private String status;

    private String userId;

    public Employee() {
    }

    public Employee(String fullName, String email, String phoneNumber, String department, String basicPay,
                    String imageUrl, String status, String userId) {

        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.basicPay = basicPay;
        this.imageUrl = imageUrl;
        this.status = status;
        this.userId = userId;
    }


    public String getFullName() {

        return fullName;
    }

    public void setFullName(String fullName) {

        this.fullName = fullName;
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

    public String getDepartment() {

        return department;
    }

    public void setDepartment(String department) {

        this.department = department;
    }

    public String getBasicPay() {
        return basicPay;
    }

    public void setBasicPay(String basicPay) {

        this.basicPay = basicPay;
    }

    public String getImageUrl() {

        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {

        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
