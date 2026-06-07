package com.hymer.hymarket.dto;

import lombok.Data;

@Data
public class ReviewRequestDto {
    private long bookingId;
    private Integer rating ;
    private String comment;

}


