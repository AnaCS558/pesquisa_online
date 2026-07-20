package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.entity.Resposta;
import com.ana.pesquisa_online.repository.RespostaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RespostaService {

    private final RespostaRepository respostaRepository;

    public RespostaService(RespostaRepository respostaRepository) {
        this.respostaRepository = respostaRepository;
    }

    public List<Resposta> listarTodas() {
        return respostaRepository.findAll();
    }

    public Resposta buscarPorId(Long id) {
        return respostaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resposta não encontrada."));
    }

    public Resposta salvar(Resposta resposta) {
        return respostaRepository.save(resposta);
    }

    public void excluir(Long id) {

        Resposta resposta = buscarPorId(id);

        respostaRepository.delete(resposta);
    }
}
