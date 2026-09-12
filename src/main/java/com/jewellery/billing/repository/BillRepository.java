package com.jewellery.billing.repository;

import com.jewellery.billing.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
	List<Bill> findByShopIdOrderByCreatedAtDesc(Long shopId);
}
