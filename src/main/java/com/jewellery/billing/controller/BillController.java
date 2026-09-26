package com.jewellery.billing.controller;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jewellery.billing.dto.BillRequest;
import com.jewellery.billing.model.Bill;
import com.jewellery.billing.service.BillPdfService;
import com.jewellery.billing.service.BillingService;

@RestController
@RequestMapping("/api/shops/{shopId}/bills")
public class BillController {

	private final BillingService service;
	private final BillPdfService pdfService;

	public BillController(BillingService service, BillPdfService pdfService) {
		this.service = service;
		this.pdfService = pdfService;
	}

	@PostMapping
	public Bill create(@PathVariable Long shopId, @RequestBody BillRequest request) {

		return service.create(shopId, request);
	}

	@GetMapping
	public List<Bill> history(@PathVariable Long shopId) {

		return service.history(shopId);
	}

	@GetMapping("/{billId}/pdf")
	public ResponseEntity<byte[]> generatePdf(@PathVariable Long shopId, @PathVariable Long billId) {

		Bill bill = service.getBillForShop(shopId, billId);

		byte[] pdf = pdfService.generatePdf(bill);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + bill.getBillNumber() + ".pdf\"")
				.body(pdf);
	}
}