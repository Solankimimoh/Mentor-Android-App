package com.example.mentor;

public class MentorDetailsModel {

    public String qualification;
    public String experience;
    public String mentorType;


    public MentorDetailsModel() {
    }

    public MentorDetailsModel(String qualification, String experience, String mentorType) {
        this.qualification = qualification;
        this.experience = experience;
        this.mentorType = mentorType;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getMentorType() {
        return mentorType;
    }

    public void setMentorType(String mentorType) {
        this.mentorType = mentorType;
    }
}
