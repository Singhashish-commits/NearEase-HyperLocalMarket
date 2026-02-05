package com.hymer.hymarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSearchDto {
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private double minRating;
    private String searchKeyword;

    private String sortBy;
    private String sortDirn;

}
