package com.jewellery.billing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shops")
public class Shop {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String username;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String shopName;
	private String address;
	private String phone;
	private String gstNumber;

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String v) {
		username = v;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String v) {
		password = v;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String v) {
		shopName = v;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String v) {
		address = v;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String v) {
		phone = v;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String v) {
		gstNumber = v;
	}
}
