package com.example.whatsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.whatsapp.model.Potencial;

import java.util.UUID;

public interface PotencialRepository extends JpaRepository<Potencial, UUID> {
}
