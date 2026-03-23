package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.ProviderProfile;
import com.hymer.hymarket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderProfileRepository  extends JpaRepository<ProviderProfile,Integer> {
    Optional<ProviderProfile> findByUser(User user);

    Double getRatingById(long id);
}
