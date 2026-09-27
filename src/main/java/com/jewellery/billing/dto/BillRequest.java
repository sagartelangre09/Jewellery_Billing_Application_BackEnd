package com.jewellery.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record BillRequest(
        @JsonProperty("customerName") String customerName,
        @JsonProperty("customerPhone") String customerPhone,
        @JsonProperty("taxPercent") BigDecimal taxPercent,
        @JsonProperty("discount") BigDecimal discount,
        @JsonProperty("items") List<Item> items,
        @JsonProperty("salesmanName") String salesmanName,
        @JsonProperty("billedBy") String billedBy,
        @JsonProperty("customerAddress") String customerAddress,
        @JsonProperty("customerPan") String customerPan,
        @JsonProperty("urdNumber") String urdNumber,
        @JsonProperty("subtotal") BigDecimal subtotal,
        @JsonProperty("netPayable") BigDecimal netPayable
) {
    public record Item(
            @JsonProperty("itemName") String itemName,
            @JsonProperty("metal") String metal,
            @JsonProperty("grossWeight") BigDecimal grossWeight,
            @JsonProperty("purity") String purity,
            @JsonProperty("ratePerGram") BigDecimal ratePerGram,
            @JsonProperty("makingCharges") BigDecimal makingCharges
    ) {}
}