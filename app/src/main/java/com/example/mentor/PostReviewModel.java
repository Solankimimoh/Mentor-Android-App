package com.example.mentor;

public class PostReviewModel {

    private String postPushKey;
    private String reviewAuthor;
    private String reviewComment;

    public PostReviewModel() {
    }

    public PostReviewModel(String postPushKey, String reviewAuthor, String reviewComment) {
        this.postPushKey = postPushKey;
        this.reviewAuthor = reviewAuthor;
        this.reviewComment = reviewComment;
    }

    public String getPostPushKey() {
        return postPushKey;
    }

    public void setPostPushKey(String postPushKey) {
        this.postPushKey = postPushKey;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
}

