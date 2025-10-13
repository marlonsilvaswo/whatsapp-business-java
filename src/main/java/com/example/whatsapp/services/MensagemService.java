package com.example.whatsapp.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.example.whatsapp.model.Contato;
import com.example.whatsapp.model.Mensagem;
import com.example.whatsapp.repository.ContatoRepository;
import com.example.whatsapp.repository.MensagemRepository;

@Service
public class MensagemService {

	private final ContatoRepository contatoRepo;
	private final MensagemRepository mensagemRepo;
	

	private final String WHATSAPP_API_URL = "https://graph.facebook.com/v17.0/";
    private final String TOKEN = "TOKEN_DEFINITIVO";


	public MensagemService(ContatoRepository contatoRepo, MensagemRepository mensagemRepo) {
		this.contatoRepo = contatoRepo;
		this.mensagemRepo = mensagemRepo;
	}

	public void processarWebhook(Map<String, Object> payload) {
		List<Map<String, Object>> entryList = (List<Map<String, Object>>) payload.get("entry");

		for (Map<String, Object> entry : entryList) {
			List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");
			for (Map<String, Object> change : changes) {
				Map<String, Object> value = (Map<String, Object>) change.get("value");

				// Processar contatos e mensagens
				List<Map<String, Object>> contacts = (List<Map<String, Object>>) value.get("contacts");
				List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");

				if (contacts != null && messages != null) {
					for (int i = 0; i < contacts.size(); i++) {
						Map<String, Object> contact = contacts.get(i);
						String waId = (String) contact.get("wa_id");
						String nome = (String) ((Map<String, Object>) contact.get("profile")).get("name");

						// Buscar ou criar contato
						Contato contato = contatoRepo.findById(waId).orElseGet(() -> {
							Contato novo = new Contato();
							novo.setWaId(waId);
							novo.setNome(nome);
							return contatoRepo.save(novo);
						});

						for (Map<String, Object> msg : messages) {
							String mensagemId = (String) msg.get("id");
							String timestamp = (String) msg.get("timestamp");
							String tipo = (String) msg.get("type");
							/*String texto = msg.containsKey("text")
									? (String) ((Map<String, Object>) msg.get("text")).get("body")
									: "";*/

							Mensagem mensagem = new Mensagem();
							mensagem.setMensagemId(mensagemId);
							mensagem.setTimestamp(timestamp);
							mensagem.setTipo(tipo);
							//mensagem.setTexto(texto);
							mensagem.setStatus("recebida");
							mensagem.setContato(contato);
							
							

							if ("text".equals(tipo)) {
							        String texto = (String) ((Map<String, Object>) msg.get("text")).get("body");
							        mensagem.setTexto(texto);
							} else if ("audio".equals(tipo)) {
							        Map<String, Object> audio = (Map<String, Object>) msg.get("audio");
							        String mediaId = (String) audio.get("id");
							        mensagem.setMediaId(mediaId); // campo novo na entidade Mensagem
							        mensagem.setTexto("[Áudio recebido]");
							        
							        //media url
							        try {
										mensagem.setMediaUrl(obterMediaUrl(mediaId));
									} catch (IOException e) {
										e.printStackTrace();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
							}


							mensagemRepo.save(mensagem);
						}
					}
				} else {

					processarWebhookAdmin(payload);

				}
			}
		}
	}

	public void processarWebhookAdmin(Map<String, Object> payload) {
		List<Map<String, Object>> entryList = (List<Map<String, Object>>) payload.get("entry");
		for (Map<String, Object> entry : entryList) {
			List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");
			for (Map<String, Object> change : changes) {
				Map<String, Object> value = (Map<String, Object>) change.get("value");

				List<Map<String, Object>> statuses = (List<Map<String, Object>>) value.get("statuses");
				if (statuses != null) {
					for (Map<String, Object> status : statuses) {
						String waId = (String) status.get("recipient_id");
						String mensagemId = (String) status.get("id");
						String statusMsg = (String) status.get("status");
						String timestamp = (String) status.get("timestamp");

						// Buscar ou criar contato
						Contato contato = contatoRepo.findById(waId).orElseGet(() -> {
							Contato novo = new Contato();
							novo.setWaId("5541987882976");
							novo.setNome("Vida Leve Estação"); // Nome não está presente no payload de status
							return contatoRepo.save(novo);
						});

						// Criar mensagem
						Mensagem mensagem = new Mensagem();
						mensagem.setMensagemId(mensagemId);
						mensagem.setStatus(statusMsg);
						mensagem.setTimestamp(timestamp);
						mensagem.setTipo("status");
						mensagem.setTexto("Status da mensagem: " + statusMsg);
						mensagem.setContato(contato);

						mensagemRepo.save(mensagem);
					}
				}
			}
		}
	}
	

	public String obterMediaUrl(String mediaId) throws IOException, InterruptedException {
	        String url = WHATSAPP_API_URL + mediaId;
	
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Authorization", "Bearer " + TOKEN)
	            .GET()
	            .build();
	
	        HttpClient client = HttpClient.newHttpClient();
	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	
	        JSONObject json = new JSONObject(response.body());
	        return json.getString("url");
	    }


}