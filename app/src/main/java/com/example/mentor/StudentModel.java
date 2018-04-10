package com.example.mentor;

/**
 * Created by solan on 14-02-18.
 */

public class StudentModel {

    public String fullname;
    public String email;
    public String password;
    public String mobile;
    public String industry;


    public StudentModel() {
    }
    public StudentModel(String fullname, String email, String password, String mobile, String industry) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.industry = industry;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }


}
