package com.hymer.hymarket.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_types")
public class ServiceType { // type of service that come under the category semiLux and Lux
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name; // plumbing , saloon, Electrician
    @ManyToOne
    @JoinColumn(name ="category_id", nullable = false)
    @JsonBackReference
    private ServiceCategory category;
     public ServiceType(String name , ServiceCategory category) {
         this.name = name;
         this.category = category;
     }


}
