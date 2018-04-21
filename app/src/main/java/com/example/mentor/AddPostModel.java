package com.example.mentor;

public class AddPostModel {

    private String title;
    private String description;
    private String imagename;
    private String imageurl;
    private String mentorname;
    private String industry;
    private String postdate;


    public AddPostModel() {
    }

    public AddPostModel(String title, String description, String imagename, String imageurl, String mentorname, String industry, String postdate) {
        this.title = title;
        this.description = description;
        this.imagename = imagename;
        this.imageurl = imageurl;
        this.mentorname = mentorname;
        this.industry = industry;
        this.postdate = postdate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getMentorname() {
        return mentorname;
    }

    public void setMentorname(String mentorname) {
        this.mentorname = mentorname;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }
}
