package com.example.meterreadercwd;

public class Applicant_Helper {
    private String account_number;
    private String applicant_name;

    public Applicant_Helper(String account_number, String applicant_name) {
        this.account_number = account_number;
        this.applicant_name = applicant_name;
    }

    public String getAcount_number() {
        return account_number;
    }

    public void setAcount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getApplicant_name() {
        return applicant_name;
    }

    public void setApplicant_name(String applicant_name) {
        this.applicant_name = applicant_name;
    }
}
