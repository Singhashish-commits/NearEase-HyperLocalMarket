package com.hymer.hymarket.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProfile {
    @Id
    private Long id;

    private String bio;

    private String skills;
    private String experience;

    private String address;

    private boolean isVerified = false;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
//    @JsonManagedReference
    private User user;

    public ProviderProfile(User user) {
        this.user = user;
    }


}
