package com.example.whatsapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.whatsapp.model.Contato;

public interface ContatoRepository extends JpaRepository<Contato, String> {

	@Query("SELECT p.contato FROM Potencial p ORDER BY p.dataResposta DESC")
	List<Contato> findContatosComPotencialOrdenadosPorDataResposta();

}
