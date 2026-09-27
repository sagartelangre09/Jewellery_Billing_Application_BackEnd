package com.jewellery.billing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "bills")
public class Bill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JsonIgnoreProperties("bills")
	private Shop shop;

	@Column(nullable = false, unique = true)
	private String billNumber;

	private String urdNumber;
	private String customerName;
	private String customerAddress;
	private String customerPhone;
	private String customerPan;

	private BigDecimal subtotal;
	private BigDecimal netPayable;
	private BigDecimal discount;
	private BigDecimal urdDeduction;
	private BigDecimal tax;
	//private BigDecimal grandTotal;
	private BigDecimal cashPaid;
	private String amountInWords;

	private BigDecimal totalSaleWeight;
	private BigDecimal totalUrdWeight;

	private String salesmanName;
	private String billedBy;
	private String irnNumber;

	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<BillItem> items = new ArrayList<>();

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getUrdNumber() {
		return urdNumber;
	}

	public void setUrdNumber(String urdNumber) {
		this.urdNumber = urdNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getCustomerPan() {
		return customerPan;
	}

	public void setCustomerPan(String customerPan) {
		this.customerPan = customerPan;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getUrdDeduction() {
		return urdDeduction;
	}

	public void setUrdDeduction(BigDecimal urdDeduction) {
		this.urdDeduction = urdDeduction;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

//	public BigDecimal getGrandTotal() {
//		return grandTotal;
//	}

//	public void setGrandTotal(BigDecimal grandTotal) {
//		this.grandTotal = grandTotal;
//	}

	public BigDecimal getCashPaid() {
		return cashPaid;
	}

	public void setCashPaid(BigDecimal cashPaid) {
		this.cashPaid = cashPaid;
	}

	public String getAmountInWords() {
		return amountInWords;
	}

	public void setAmountInWords(String amountInWords) {
		this.amountInWords = amountInWords;
	}

	public BigDecimal getTotalSaleWeight() {
		return totalSaleWeight;
	}

	public void setTotalSaleWeight(BigDecimal totalSaleWeight) {
		this.totalSaleWeight = totalSaleWeight;
	}

	public BigDecimal getTotalUrdWeight() {
		return totalUrdWeight;
	}

	public void setTotalUrdWeight(BigDecimal totalUrdWeight) {
		this.totalUrdWeight = totalUrdWeight;
	}

	public String getSalesmanName() {
		return salesmanName;
	}

	public void setSalesmanName(String salesmanName) {
		this.salesmanName = salesmanName;
	}

	public String getBilledBy() {
		return billedBy;
	}

	public void setBilledBy(String billedBy) {
		this.billedBy = billedBy;
	}

	public String getIrnNumber() {
		return irnNumber;
	}

	public void setIrnNumber(String irnNumber) {
		this.irnNumber = irnNumber;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<BillItem> getItems() {
		return items;
	}

	public void setItems(List<BillItem> items) {
		this.items = items;
	}

	public BigDecimal getNetPayable() {
		return netPayable;
	}

	public void setNetPayable(BigDecimal netPayable) {
		this.netPayable = netPayable;
	}

}