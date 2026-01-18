package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.ProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderProfileRepository  extends JpaRepository<ProviderProfile,Integer> {
}
