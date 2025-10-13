package com.example.whatsapp;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WhatsAppSender {
    public static void main(String[] args) {
        
    	try {
    		enviarMensagemVideo("5541999111646", "https://cdn-media.f-static.net/uploads/11273271/normal_68e407c0d910b.mp4", "Boaaaa tarde Marlon");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
         
    private static OkHttpClient client = new OkHttpClient();
    private static final String token = "TOKEN_DEFINITIVO";
    private static final String phoneNumberId = "866354453217349";

    public static void enviarMensagemTexto(String telefone, String mensagem) throws IOException {
        String url = "https://graph.facebook.com/v17.0/" + phoneNumberId + "/messages";
        String json = "{\"messaging_product\":\"whatsapp\",\"to\":\""+telefone+"\",\"type\":\"text\",\"text\":{\"body\":\""+mensagem+"\"}}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Resposta: " + response.body().string());
    }

    public static void enviarMensagemImagem(String telefone, String imagemUrl, String legenda) throws IOException {
        String url = "https://graph.facebook.com/v17.0/" + phoneNumberId + "/messages";
        String json = "{\"messaging_product\":\"whatsapp\",\"to\":\""+telefone+"\",\"type\":\"image\",\"image\":{\"link\":\""+imagemUrl+"\",\"caption\":\""+legenda+"\"}}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Resposta imagem: " + response.body().string());
    }
    
    public static void enviarMensagemVideo(String telefone, String videoUrl, String legenda) throws IOException {
        String url = "https://graph.facebook.com/v17.0/" + phoneNumberId + "/messages";
        String json = "{"
            + "\"messaging_product\":\"whatsapp\","
            + "\"to\":\"" + telefone + "\","
            + "\"type\":\"video\","
            + "\"video\":{"
            + "\"link\":\"" + videoUrl + "\","
            + "\"caption\":\"" + legenda + "\""
            + "}"
            + "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer " + token)
            .addHeader("Content-Type", "application/json")
            .build();

        Response response = client.newCall(request).execute();
        System.out.println("Resposta vídeo: " + response.body().string());
    }

}