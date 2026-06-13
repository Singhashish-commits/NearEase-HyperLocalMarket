package com.hymer.hymarket.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReviewReplyDto {
    @Size(max = 1000, message = "Reply cannot exceed 1000 characters")
    String reply;
}
