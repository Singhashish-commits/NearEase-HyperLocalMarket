package com.hymer.hymarket.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDto {
    private long id;
    private Integer rating;
    private String comment;
    private String CustomerName;
    private LocalDateTime createdAt;

}
