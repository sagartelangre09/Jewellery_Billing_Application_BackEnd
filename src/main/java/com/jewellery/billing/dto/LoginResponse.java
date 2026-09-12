package com.jewellery.billing.dto;

public record LoginResponse(Long shopId, String username, String shopName, String address, String phone,
		String gstNumber, String token) {
}
