package com.example.mobilepayroll;

public class PayModel {
    public PayModel(){
    }
    String FullName, Status, NetPay, TotalEarnings, TotalDeductions, PayrollTitle, Email, Department;
    public PayModel(String FullName, String Status, String NetPay,
                    String TotalEarnings, String TotalDeductions, String PayrollTitle,
                    String Email, String Department) {
        this.FullName =FullName;
        this.Status = Status;
        this.NetPay = NetPay;
        this.TotalEarnings = TotalEarnings;
        this.TotalDeductions = TotalDeductions;
        this.PayrollTitle = PayrollTitle;
        this.Email = Email;
        this.Department = Department;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getNetPay() {
        return NetPay;
    }

    public void setNetPay(String netPay) {
        NetPay = netPay;
    }

    public String getTotalEarnings() {
        return TotalEarnings;
    }

    public void setTotalEarnings(String totalEarnings) {
        TotalEarnings = totalEarnings;
    }

    public String getTotalDeductions() {
        return TotalDeductions;
    }

    public void setTotalDeductions(String totalDeductions) {
        TotalDeductions = totalDeductions;
    }

    public String getPayrollTitle() {
        return PayrollTitle;
    }

    public void setPayrollTitle(String payrollTitle) {
        PayrollTitle = payrollTitle;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }
}
