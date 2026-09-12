package com.jewellery.billing.dto;

import java.math.BigDecimal;
import java.util.List;

public record BillRequest(String customerName, String customerPhone, BigDecimal taxPercent, BigDecimal discount,
		List<Item> items,String salesmanName,String billedBy, String customerAddress,String customerPan,String urdNumber) {
	public record Item(String itemName, String metal, BigDecimal grossWeight, BigDecimal stoneWeight,
			BigDecimal ratePerGram, BigDecimal makingCharges, BigDecimal stoneCharges) {
	}
}
