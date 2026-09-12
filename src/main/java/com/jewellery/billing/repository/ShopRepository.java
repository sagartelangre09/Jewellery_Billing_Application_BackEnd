package com.jewellery.billing.repository;

import com.jewellery.billing.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
	Optional<Shop> findByUsername(String username);
}
