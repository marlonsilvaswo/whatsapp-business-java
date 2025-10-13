package com.example.whatsapp.services;


import org.springframework.stereotype.Service;

import com.example.whatsapp.model.Potencial;
import com.example.whatsapp.repository.PotencialRepository;

import java.util.List;
import java.util.UUID;

@Service
public class PotencialService {

    private final PotencialRepository repository;

    public PotencialService(PotencialRepository repository) {
        this.repository = repository;
    }

    public List<Potencial> listarTodos() {
        return repository.findAll();
    }

    public Potencial salvar(Potencial potencial) {
        return repository.save(potencial);
    }

    public Potencial buscarPorId(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}
