package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.entity.Pesquisa;
import com.ana.pesquisa_online.repository.PesquisaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PesquisaService {

    private final PesquisaRepository repository;

    public PesquisaService(PesquisaRepository repository) {
        this.repository = repository;
    }

    public Pesquisa salvar(Pesquisa pesquisa) {
        return repository.save(pesquisa);
    }

    public List<Pesquisa> listar() {
        return repository.findAll();
    }
}
