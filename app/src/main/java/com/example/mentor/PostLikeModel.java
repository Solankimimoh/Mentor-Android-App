package com.example.mentor;

public class PostLikeModel
{
    private String likerUser;

    public PostLikeModel() {
    }

    public PostLikeModel(String likerUser) {
        this.likerUser = likerUser;
    }

    public String getLikerUser() {
        return likerUser;
    }

    public void setLikerUser(String likerUser) {
        this.likerUser = likerUser;
    }
}
