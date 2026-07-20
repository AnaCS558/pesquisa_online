package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.entity.Pergunta;
import com.ana.pesquisa_online.repository.PerguntaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerguntaService {


    private final PerguntaRepository perguntaRepository;

    public PerguntaService(PerguntaRepository perguntaRepository) {
        this.perguntaRepository = perguntaRepository;
    }

    public List<Pergunta> listarTodas() {
        return perguntaRepository.findAll();
    }

    public Pergunta buscarPorId(Long id) {
        return perguntaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pergunta não encontrada."));
    }

    public Pergunta salvar(Pergunta pergunta) {
        return perguntaRepository.save(pergunta);
    }

    public Pergunta atualizar(Long id, Pergunta perguntaAtualizada) {

        Pergunta pergunta = buscarPorId(id);

        pergunta.setEnunciado(perguntaAtualizada.getEnunciado());
        pergunta.setTipoPergunta(perguntaAtualizada.getTipoPergunta());
        pergunta.setObrigatoria(perguntaAtualizada.getObrigatoria());
        pergunta.setOrdem(perguntaAtualizada.getOrdem());

        return perguntaRepository.save(pergunta);
    }

    public void excluir(Long id) {

        Pergunta pergunta = buscarPorId(id);

        perguntaRepository.delete(pergunta);
    }
}

