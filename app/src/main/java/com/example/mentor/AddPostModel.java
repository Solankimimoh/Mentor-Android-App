package com.example.mentor;

public class AddPostModel {

    private String title;
    private String description;
    private String filename;
    private String fileurl;
    private String fileThumburl;
    private String username;
    private String industry;
    private String postdate;
    private String postPushKey;
    private String postUserAvatar;

    public AddPostModel() {
    }

    public AddPostModel(String title, String description, String filename, String fileurl, String fileThumburl, String username, String industry, String postdate, String postPushKey, String postUserAvatar) {
        this.title = title;
        this.description = description;
        this.filename = filename;
        this.fileurl = fileurl;
        this.fileThumburl = fileThumburl;
        this.username = username;
        this.industry = industry;
        this.postdate = postdate;
        this.postPushKey = postPushKey;
        this.postUserAvatar = postUserAvatar;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getFileThumburl() {
        return fileThumburl;
    }

    public void setFileThumburl(String fileThumburl) {
        this.fileThumburl = fileThumburl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPostPushKey() {
        return postPushKey;
    }

    public void setPostPushKey(String postPushKey) {
        this.postPushKey = postPushKey;
    }

    public String getPostUserAvatar() {
        return postUserAvatar;
    }

    public void setPostUserAvatar(String postUserAvatar) {
        this.postUserAvatar = postUserAvatar;
    }
}
