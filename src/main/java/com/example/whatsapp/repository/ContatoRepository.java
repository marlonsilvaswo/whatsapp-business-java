package com.example.whatsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.whatsapp.model.Contato;

public interface ContatoRepository extends JpaRepository<Contato, String> {
}
