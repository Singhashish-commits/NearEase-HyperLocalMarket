package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.ProviderProfile;
import com.hymer.hymarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderProfileRepository  extends JpaRepository<ProviderProfile,Long> {

    Optional<ProviderProfile> findByUser(User user);

    @Query("SELECT p.averageRating FROM ProviderProfile p WHERE p.id = :id")
    Double getRatingById(@Param("id") long id);

    @Query("SELECT p.averageRating FROM ProviderProfile p WHERE p.id = :id")
    Double findAverageRatingById(@Param("id") Long id);

    List<ProviderProfile> findByIsVerified(boolean isVerified);
}
