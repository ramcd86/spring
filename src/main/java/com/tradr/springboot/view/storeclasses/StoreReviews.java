package com.tradr.springboot.view.storeclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreReviews {
    private String parentUUID;
    private int storeRating;
    private String from;
    private String review;
}
