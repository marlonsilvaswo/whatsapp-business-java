package com.example.whatsapp.services;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.whatsapp.model.Contato;
import com.example.whatsapp.repository.ContatoRepository;

@Service
public class ContatoService {

    private final ContatoRepository contatoRepository;
    
    public ContatoService(ContatoRepository contatoRepository) {
        this.contatoRepository = contatoRepository;
    }

    public List<Contato> listarContatosComPotencialOrdenados() {
        return contatoRepository.findContatosComPotencialOrdenadosPorDataResposta();
    }
}
