package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.entity.Pesquisa;
import com.ana.pesquisa_online.repository.PesquisaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PesquisaService {

    private final PesquisaRepository pesquisaRepository;

    public PesquisaService(PesquisaRepository pesquisaRepository) {
        this.pesquisaRepository = pesquisaRepository;
    }

    public List<Pesquisa> listarTodas() {
        return pesquisaRepository.findAll();
    }

    public Pesquisa buscarPorId(Long id) {
        return pesquisaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pesquisa não encontrada."));
    }

    public Pesquisa salvar(Pesquisa pesquisa) {
        return pesquisaRepository.save(pesquisa);
    }

    public Pesquisa atualizar(Long id, Pesquisa pesquisaAtualizada) {

        Pesquisa pesquisa = buscarPorId(id);

        pesquisa.setTitulo(pesquisaAtualizada.getTitulo());
        pesquisa.setDescricao(pesquisaAtualizada.getDescricao());
        pesquisa.setAtiva(pesquisaAtualizada.getAtiva());
        pesquisa.setDataEncerramento(pesquisaAtualizada.getDataEncerramento());

        return pesquisaRepository.save(pesquisa);
    }

    public void excluir(Long id) {

        if (!pesquisaRepository.existsById(id)) {
            throw new RuntimeException("Pesquisa não encontrada.");
        }

        pesquisaRepository.deleteById(id);
    }
}