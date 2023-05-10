package com.hellokoding.springboot.view.storeclasses;

public class StoreReviews {
    private String parentUUID;
    private int storeRating;
    private String from;
    private String review;

    public String getParentUUID() {
        return parentUUID;
    }

    public void setParentUUID(String parentUUID) {
        this.parentUUID = parentUUID;
    }

    public int getStoreRating() {
        return storeRating;
    }

    public void setStoreRating(int storeRating) {
        this.storeRating = storeRating;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }


    @Override
    public String toString() {
        return "StoreReviews{" +
                "parentUUID='" + parentUUID + '\'' +
                ", storeRating=" + storeRating +
                ", from='" + from + '\'' +
                ", review='" + review + '\'' +
                '}';
    }
}
