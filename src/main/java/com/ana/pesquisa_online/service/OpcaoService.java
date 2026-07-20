package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.entity.Opcao;
import com.ana.pesquisa_online.repository.OpcaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpcaoService {

    private final OpcaoRepository opcaoRepository;

    public OpcaoService(OpcaoRepository opcaoRepository) {
        this.opcaoRepository = opcaoRepository;
    }

    public List<Opcao> listarTodas() {
        return opcaoRepository.findAll();
    }

    public Opcao buscarPorId(Long id) {
        return opcaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opção não encontrada."));
    }

    public Opcao salvar(Opcao opcao) {
        return opcaoRepository.save(opcao);
    }

    public Opcao atualizar(Long id, Opcao opcaoAtualizada) {

        Opcao opcao = buscarPorId(id);

        opcao.setDescricao(opcaoAtualizada.getDescricao());

        return opcaoRepository.save(opcao);
    }

    public void excluir(Long id) {

        Opcao opcao = buscarPorId(id);

        opcaoRepository.delete(opcao);
    }
}

