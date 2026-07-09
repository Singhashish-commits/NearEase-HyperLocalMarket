package com.hymer.hymarket.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDto {
    private Long id;
    private Integer rating;
    private String serviceTitle;
    @Size(max = 1000, message = "Reply cannot exceed 1000 characters")
    private String comment;
    private String CustomerName;
    private LocalDateTime createdAt;
    @Size(max = 1000, message = "Reply cannot exceed 1000 characters")
    private String providerReply;

    private LocalDateTime repliedAt;


}
