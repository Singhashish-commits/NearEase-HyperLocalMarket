package com.hymer.hymarket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "service_offerings")
public class ServiceOfferingIndex {
    @Id
    private String id; // Elastic always stores IDs as Strings

    @Field(type = FieldType.Text, analyzer = "standard")
    private String serviceTitle;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String serviceName;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String providerProfileName; // Flattened data!

    @Field(type = FieldType.Keyword) // Use Keyword for exact URL strings
    private String imageUrl;

    @Field(type = FieldType.Double)
    private Double rating;

    @GeoPointField
    private GeoPoint location;


}
