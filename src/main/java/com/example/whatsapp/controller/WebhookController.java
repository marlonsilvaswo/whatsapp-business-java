package com.example.whatsapp.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.whatsapp.repository.MensagemRepository;
import com.example.whatsapp.services.MensagemService;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

	private final MensagemService mensagemService;
	private final MensagemRepository mensagemRepo;

	public WebhookController(MensagemService mensagemService, MensagemRepository mensagemRepo) {
        this.mensagemService = mensagemService;
        this.mensagemRepo = mensagemRepo;
    }

	@PostMapping
	public ResponseEntity<String> receberMensagem(@RequestBody Map<String, Object> payload) {
		System.out.println("Mensagem recebida: " + payload);
		
		mensagemService.processarWebhook(payload);
		
		return ResponseEntity.ok("EVENT_RECEIVED");
	}

	@GetMapping
	public ResponseEntity<String> receberMensagem(
			@RequestParam(name = "hub.mode") String mode,
			@RequestParam(name = "hub.verify_token") String token,
			@RequestParam(name = "hub.challenge") String challenge) {

		String VERIFY_TOKEN = "TOKEN_VERIFICACAO";

		if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
			return ResponseEntity.ok(challenge);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido");
		}

	}
}