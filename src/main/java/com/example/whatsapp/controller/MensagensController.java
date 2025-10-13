package com.example.whatsapp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.whatsapp.WhatsAppSender;
import com.example.whatsapp.model.Contato;
import com.example.whatsapp.model.ContatoDTO;
import com.example.whatsapp.model.Mensagem;
import com.example.whatsapp.model.MensagemDTO;
import com.example.whatsapp.repository.ContatoRepository;
import com.example.whatsapp.repository.MensagemRepository;
import com.example.whatsapp.services.MensagemService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/mensagens")
public class MensagensController {

	private final MensagemService mensagemService;
	private final MensagemRepository mensagemRepo;
	private final ContatoRepository contatoRepo;

	public MensagensController(MensagemService mensagemService, MensagemRepository mensagemRepo,
			ContatoRepository contatoRepo) {
		this.mensagemService = mensagemService;
		this.mensagemRepo = mensagemRepo;
		this.contatoRepo = contatoRepo;
	}

	// 🔹 GET /mensagens
	@Operation(summary = "Lista de todas as mensagens")
	@GetMapping
	public ResponseEntity<List<MensagemDTO>> listarTodasMensagens() {

		List<Mensagem> mensagens = mensagemRepo.findAll();
		List<MensagemDTO> mensagensDto = new ArrayList<MensagemDTO>();

		for (Mensagem mensagem : mensagens) {
			MensagemDTO msg = new MensagemDTO();
			msg.setTexto(mensagem.getTexto());
			msg.setMensagemId(mensagem.getMensagemId());
			msg.setStatus(mensagem.getStatus());
			msg.setTipo(mensagem.getTipo());
			msg.setId(mensagem.getId());
			msg.setWaId(mensagem.getContato().getWaId());
			mensagensDto.add(msg);
		}

		return ResponseEntity.ok(mensagensDto);
	}

	// 🔹 POST /mensagens
	@Operation(summary = "Responder uma mensagem")
	@PostMapping
	public ResponseEntity<String> enviarMensagem(@RequestBody Map<String, Object> payload) {

		// Extrair dados do Map
		String texto = (String) payload.get("texto");
		String tipo = (String) payload.get("tipo");
		String waId = (String) payload.get("waId");
		String status = (String) payload.get("status");

		// mensagem
		Mensagem mensagem = new Mensagem();
		// mensagem.setMensagemId(mensagemId);
		mensagem.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));
		mensagem.setTipo(tipo);
		mensagem.setTexto(texto);
		mensagem.setStatus(status);

		Contato contato = contatoRepo.findById(waId).orElseGet(() -> {
			Contato novo = new Contato();
			novo.setWaId(waId);
			novo.setNome("");
			return contatoRepo.save(novo);
		});

		mensagem.setContato(contato);
		mensagemRepo.save(mensagem);

		try {
			WhatsAppSender.enviarMensagemTexto(waId, texto);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok("EVENT_RECEIVED_ADM");
	}

	// 🔹 GET /mensagens/contatos
	@Operation(summary = "Lista de todos os contatos")
	@GetMapping("/contatos")
	public ResponseEntity<List<ContatoDTO>> listarContatos() {

		List<ContatoDTO> listarContatosDto = new ArrayList<ContatoDTO>();

		List<Contato> listarContatos = contatoRepo.findAll();

		for (Contato cts : listarContatos) {
			ContatoDTO contatoDto = new ContatoDTO();
			contatoDto.setNome(cts.getNome());
			contatoDto.setNumero(cts.getWaId());
			listarContatosDto.add(contatoDto);
		}

		return ResponseEntity.ok(listarContatosDto);
	}
	
	@Operation(summary = "Lista de todos os contatos com potencial que responderam")
	@GetMapping("/com-potencial")
	public ResponseEntity<List<ContatoDTO>> listarContatosComPotencial() {
		
		List<ContatoDTO> listarContatosDto = new ArrayList<ContatoDTO>();
		
		List<Contato> listarContatos = contatoRepo.findContatosComPotencialOrdenadosPorDataResposta();

		for (Contato cts : listarContatos) {
			ContatoDTO contatoDto = new ContatoDTO();
			contatoDto.setNome(cts.getNome());
			contatoDto.setNumero(cts.getWaId());
			listarContatosDto.add(contatoDto);
		}

		return ResponseEntity.ok(listarContatosDto);
	}
	
	

	// 🔹 GET /mensagens/contato/{numero}
	@Operation(summary = "Lista mensagens de um contato")
	@GetMapping("/contato/{waId}")
	public ResponseEntity<List<MensagemDTO>> listarMensagensPorContato(@PathVariable String waId) {

		List<Mensagem> mensagens = mensagemRepo.findByContatoWaId(waId);

		List<MensagemDTO> mensagensDto = new ArrayList<MensagemDTO>();

		for (Mensagem mensagem : mensagens) {
			MensagemDTO msg = new MensagemDTO();
			msg.setTexto(mensagem.getTexto());
			msg.setMensagemId(mensagem.getMensagemId());
			msg.setStatus(mensagem.getStatus());
			msg.setTipo(mensagem.getTipo());
			msg.setId(mensagem.getId());
			msg.setWaId(mensagem.getContato().getWaId());
			msg.setMediaId(mensagem.getMediaId());
			msg.setMediaUrl(mensagem.getMediaUrl());
			mensagensDto.add(msg);
		}

		return ResponseEntity.ok(mensagensDto);
	}

	@GetMapping("/audio2/{mediaId}")
	public ResponseEntity<byte[]> servirAudio(@PathVariable String mediaId) {
		try {
			String url = "https://graph.facebook.com/v19.0/" + mediaId;

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Authorization", "Bearer "
					+ "TOKEN_DEFINITIVO")
					.GET().build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.valueOf("audio/ogg")); // ou "audio/mpeg" dependendo do tipo

			return new ResponseEntity<>(response.body(), headers, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private String TOKEN = "TOKEN_DEFINITIVO";

	@GetMapping("/audio/{mediaId}")
	public ResponseEntity<byte[]> servirAudioConvertido(@PathVariable String mediaId) {
	    String token = "Bearer " + TOKEN;
	    String mediaInfoUrl = "https://graph.facebook.com/v17.0/" + mediaId;

	    Path oggPath = null;
	    Path mp3Path = null;

	    try {
	        // 1. Obter URL da mídia via API do WhatsApp
	        HttpRequest infoRequest = HttpRequest.newBuilder()
	                .uri(URI.create(mediaInfoUrl))
	                .header("Authorization", token)
	                .GET()
	                .build();

	        HttpClient client = HttpClient.newHttpClient();
	        HttpResponse<String> infoResponse = client.send(infoRequest, HttpResponse.BodyHandlers.ofString());

	        if (infoResponse.statusCode() != 200) {
	            System.out.println("Erro ao obter informações da mídia");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        }

	        // 2. Extrair URL da mídia
	        JSONObject json = new JSONObject(infoResponse.body());
	        String mediaUrl = json.getString("url");

	        // 3. Baixar o arquivo OGG
	        HttpRequest mediaRequest = HttpRequest.newBuilder()
	                .uri(URI.create(mediaUrl))
	                .header("Authorization", token)
	                .GET()
	                .build();

	        HttpResponse<byte[]> mediaResponse = client.send(mediaRequest, HttpResponse.BodyHandlers.ofByteArray());

	        if (mediaResponse.statusCode() != 200) {
	            System.out.println("Erro ao baixar a mídia");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }

	        // 4. Salvar o arquivo OGG temporariamente
	        oggPath = Files.createTempFile("audio", ".ogg");
	        Files.write(oggPath, mediaResponse.body());

	        // 5. Definir caminho para o arquivo MP3
	        mp3Path = Paths.get(oggPath.toString().replace(".ogg", ".mp3"));

	        // 6. Converter OGG para MP3 usando ffmpeg
	        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-y", "-i", oggPath.toString(), "-codec:a", "libmp3lame", "-qscale:a", "2", mp3Path.toString());
	        pb.redirectErrorStream(true);
	        Process process = pb.start();

	        // Log da conversão
	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                System.out.println(line);
	            }
	        }

	        int exitCode = process.waitFor();
	        if (exitCode != 0 || !Files.exists(mp3Path)) {
	            System.out.println("Falha na conversão com ffmpeg");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }

	        // 7. Ler o conteúdo do MP3
	        byte[] mp3Bytes = Files.readAllBytes(mp3Path);

	        // 8. Retornar o áudio convertido
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.valueOf("audio/mpeg")); // MP3 é compatível com React Native
	        headers.setContentLength(mp3Bytes.length);

	        return new ResponseEntity<>(mp3Bytes, headers, HttpStatus.OK);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    } finally {
	        // 9. Limpar arquivos temporários
	        try {
	            if (oggPath != null) Files.deleteIfExists(oggPath);
	            if (mp3Path != null) Files.deleteIfExists(mp3Path);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}


}
