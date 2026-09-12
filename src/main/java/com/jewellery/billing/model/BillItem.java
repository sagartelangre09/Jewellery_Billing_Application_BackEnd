package com.jewellery.billing.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bill_items")
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String hsn;
    private String purity;
    private Integer pieces;
    private String metal;
    
    private BigDecimal grossWeight;
    private BigDecimal stoneWeight;
    private BigDecimal netWeight;
    private BigDecimal ratePerGram;
    private BigDecimal makingCharges;
    private BigDecimal stoneCharges;
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    @JsonBackReference
    private Bill bill;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getPurity() {
        return purity;
    }

    public void setPurity(String purity) {
        this.purity = purity;
    }

    public Integer getPieces() {
        return pieces;
    }

    public void setPieces(Integer pieces) {
        this.pieces = pieces;
    }

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    public BigDecimal getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(BigDecimal grossWeight) {
        this.grossWeight = grossWeight;
    }

    public BigDecimal getStoneWeight() {
        return stoneWeight;
    }

    public void setStoneWeight(BigDecimal stoneWeight) {
        this.stoneWeight = stoneWeight;
    }

    public BigDecimal getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(BigDecimal netWeight) {
        this.netWeight = netWeight;
    }

    public BigDecimal getRatePerGram() {
        return ratePerGram;
    }

    public void setRatePerGram(BigDecimal ratePerGram) {
        this.ratePerGram = ratePerGram;
    }

    public BigDecimal getMakingCharges() {
        return makingCharges;
    }

    public void setMakingCharges(BigDecimal makingCharges) {
        this.makingCharges = makingCharges;
    }

    public BigDecimal getStoneCharges() {
        return stoneCharges;
    }

    public void setStoneCharges(BigDecimal stoneCharges) {
        this.stoneCharges = stoneCharges;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}