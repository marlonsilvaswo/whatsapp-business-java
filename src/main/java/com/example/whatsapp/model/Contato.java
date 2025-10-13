package com.example.whatsapp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Contato {

    @Id
    private String waId; // Ex: "554199111646"

    private String nome;

    @OneToMany(mappedBy = "contato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mensagem> mensagens;

    // Getters e Setters
    public String getWaId() {
        return waId;
    }

    public void setWaId(String waId) {
        this.waId = waId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<Mensagem> mensagens) {
        this.mensagens = mensagens;
    }
}