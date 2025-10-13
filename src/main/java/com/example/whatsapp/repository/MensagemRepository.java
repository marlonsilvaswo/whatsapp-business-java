package com.example.whatsapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.whatsapp.model.Mensagem;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByContatoWaId(String waId);
}