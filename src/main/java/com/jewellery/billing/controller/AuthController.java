package com.jewellery.billing.controller;

import com.jewellery.billing.dto.*;
import com.jewellery.billing.service.BillingService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final BillingService service;

	public AuthController(BillingService s) {
		service = s;
	}

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest r) {
		return service.login(r);
	}

	@ExceptionHandler(RuntimeException.class)
	public Map<String, String> error(RuntimeException e) {
		return Map.of("message", e.getMessage());
	}
}
