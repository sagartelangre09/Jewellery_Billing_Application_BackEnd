package com.jewellery.billing.repository;

import com.jewellery.billing.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
	List<Bill> findByShopIdOrderByCreatedAtDesc(Long shopId);
	// 3. Combined search: match phone OR name with a single query parameter
    @Query("SELECT b FROM Bill b WHERE " +
           "LOWER(b.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "b.customerPhone LIKE CONCAT('%', :query, '%')")
    List<Bill> searchByCustomerNameOrPhone(@Param("query") String query);
}
