package com.example.mentor;

public class FollowingModel {
    private String name;
    private String industry;
    private boolean followABoolean;
    private boolean followingABoolean;

    public FollowingModel(String name, String industry, boolean followABoolean, boolean followingABoolean) {
        this.name = name;
        this.industry = industry;
        this.followABoolean = followABoolean;
        this.followingABoolean = followingABoolean;
    }

    public FollowingModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public boolean isFollowABoolean() {
        return followABoolean;
    }

    public void setFollowABoolean(boolean followABoolean) {
        this.followABoolean = followABoolean;
    }

    public boolean isFollowingABoolean() {
        return followingABoolean;
    }

    public void setFollowingABoolean(boolean followingABoolean) {
        this.followingABoolean = followingABoolean;
    }
}
