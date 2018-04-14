package com.example.mentor;

/**
 * Created by solan on 08-03-18.
 */

public class UserModel {
    public String fullname;
    public String email;
    public String password;
    public String mobile;
    public String industry;
    public boolean mentor;

    public UserModel() {
    }

    public UserModel(String fullname, String email, String password, String mobile, String industry, boolean mentor) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.industry = industry;
        this.mentor = mentor;
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

    public boolean isMentor() {
        return mentor;
    }

    public void setMentor(boolean mentor) {
        this.mentor = mentor;
    }
}
