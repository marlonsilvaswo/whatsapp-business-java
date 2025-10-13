package com.example.whatsapp.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.whatsapp.model.Potencial;
import com.example.whatsapp.services.PotencialService;

@RestController
@RequestMapping("/potenciais")
public class PotencialController {

    private final PotencialService service;

    public PotencialController(PotencialService service) {
        this.service = service;
    }

    @GetMapping
    public List<Potencial> listar() {
        return service.listarTodos();
    }

    @PostMapping
    public Potencial criar(@RequestBody Potencial potencial) {
        return service.salvar(potencial);
    }

    @GetMapping("/{id}")
    public Potencial buscar(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable UUID id) {
        service.deletar(id);
    }
}
