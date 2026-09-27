package com.jewellery.billing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jewellery.billing.dto.BillRequest;
import com.jewellery.billing.dto.LoginRequest;
import com.jewellery.billing.dto.LoginResponse;
import com.jewellery.billing.model.Bill;
import com.jewellery.billing.model.BillItem;
import com.jewellery.billing.model.Shop;
import com.jewellery.billing.repository.BillRepository;
import com.jewellery.billing.repository.ShopRepository;

@Service
public class BillingService {

	private final ShopRepository shops;
	private final BillRepository bills;

	public BillingService(ShopRepository s, BillRepository b) {
		shops = s;
		bills = b;
	}

	public LoginResponse login(LoginRequest r) {
		Shop s = shops.findByUsername(r.username()).orElseThrow(() -> new RuntimeException("Invalid credentials"));
		if (!s.getPassword().equals(r.password())) {
			throw new RuntimeException("Invalid credentials");
		}
		return new LoginResponse(s.getId(), s.getUsername(), s.getShopName(), s.getAddress(), s.getPhone(),
				s.getGstNumber(), "SHOP-" + s.getId());
	}

	@Transactional
	public Bill create(Long shopId, BillRequest r) {
		Shop shop = shops.findById(shopId).orElseThrow(() -> new RuntimeException("Shop not found"));

		Bill b = new Bill();
		b.setShop(shop);
		b.setBillNumber("BILL-" + System.currentTimeMillis());
		b.setCustomerName(r.customerName());
		b.setCustomerPhone(r.customerPhone());
		b.setCustomerAddress(r.customerAddress());
		b.setBilledBy(r.billedBy());
		b.setSalesmanName(r.salesmanName());
		b.setCustomerPan(r.customerPan());
		b.setCreatedAt(LocalDateTime.now());

		BigDecimal subtotal = BigDecimal.ZERO;

		for (BillRequest.Item x : r.items()) {
			BigDecimal gross = n(x.grossWeight());
			BigDecimal rate = n(x.ratePerGram());
			BigDecimal makingCharges = n(x.makingCharges());

			BigDecimal metalAmount = gross.multiply(rate);
			BigDecimal totalMakingCharges = makingCharges.multiply(gross);
			BigDecimal amount = metalAmount.add(totalMakingCharges);

			BillItem i = new BillItem();
			i.setBill(b);
			i.setItemName(x.itemName());
			i.setPurity(x.purity());
			i.setMetal(x.metal());
			i.setGrossWeight(gross);
			i.setRatePerGram(rate);
			i.setMakingCharges(makingCharges);
			i.setAmount(amount);

			b.getItems().add(i);
		}

		BigDecimal discount = n(r.discount());
		BigDecimal taxPercent = n(r.taxPercent());

		// Ensure subtotal doesn't go below zero after trade-in deductions and discounts
		BigDecimal taxable = subtotal.subtract(discount).max(BigDecimal.ZERO);
		BigDecimal tax = taxable.multiply(taxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

		b.setSubtotal(r.subtotal());
		b.setDiscount(r.discount());
		b.setTax(r.taxPercent());
		b.setNetPayable(r.netPayable());
		//b.setGrandTotal(taxable.add(tax));

		return bills.save(b);
	}

	private BigDecimal n(BigDecimal v) {
		return v == null ? BigDecimal.ZERO : v;
	}

	public List<Bill> history(Long shopId) {
		return bills.findByShopIdOrderByCreatedAtDesc(shopId);
	}

	public Bill getBillForShop(Long shopId, Long billId) {
		Bill bill = bills.findById(billId).orElseThrow(() -> new RuntimeException("Bill not found"));
		if (!bill.getShop().getId().equals(shopId)) {
			throw new RuntimeException("Bill does not belong to this shop");
		}
		return bill;
	}
}