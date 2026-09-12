package com.jewellery.billing.service;

import com.jewellery.billing.dto.*;
import com.jewellery.billing.model.*;
import com.jewellery.billing.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.*;

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
		if (!s.getPassword().equals(r.password()))
			throw new RuntimeException("Invalid credentials");
		return new LoginResponse(s.getId(), s.getUsername(), s.getShopName(), s.getAddress(), s.getPhone(),
				s.getGstNumber(), "SHOP-" + s.getId());
	}

	@Transactional
	public Bill create(Long shopId, BillRequest r) {
		Shop shop = shops.findById(shopId).orElseThrow();
		Bill b = new Bill();
		b.setShop(shop);
		b.setBillNumber("BILL-" + System.currentTimeMillis());
		b.setCustomerName(r.customerName());
		b.setCustomerPhone(r.customerPhone());
		b.setCustomerAddress(r.customerAddress());
		b.setBilledBy(r.billedBy());
		b.setSalesmanName(r.salesmanName());
		b.setCustomerPan(r.customerPan());
		b.setSalesmanName(r.salesmanName());
		b.setCreatedAt(LocalDateTime.now());
		BigDecimal subtotal = BigDecimal.ZERO;
		for (BillRequest.Item x : r.items()) {
			BigDecimal gross = n(x.grossWeight());
			// BigDecimal net = gross.subtract(stone);
			BigDecimal metal = gross.multiply(n(x.ratePerGram()));
			BigDecimal amount = metal.add(n(x.makingCharges().multiply(x.grossWeight())));
			BillItem i = new BillItem();
			i.setBill(b);
			i.setItemName(x.itemName());
			i.setMetal(x.metal());
			i.setGrossWeight(gross);
			// i.setStoneWeight(stone);
			// i.setNetWeight(net);
			i.setRatePerGram(n(x.ratePerGram()));
			i.setMakingCharges(n(x.makingCharges()));
		//	i.setStoneCharges(n(x.stoneCharges()));
			i.setAmount(amount);
			b.getItems().add(i);
			subtotal = subtotal.add(amount);
		}
		BigDecimal discount = n(r.discount()), taxPercent = n(r.taxPercent());
		BigDecimal taxable = subtotal.subtract(discount).max(BigDecimal.ZERO);
		BigDecimal tax = taxable.multiply(taxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		b.setSubtotal(subtotal);
		b.setDiscount(discount);
		b.setTax(tax);
		b.setGrandTotal(taxable.add(tax));
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
