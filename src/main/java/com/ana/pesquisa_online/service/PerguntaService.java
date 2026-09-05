package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.dto.PerguntaRequestDTO;
import com.ana.pesquisa_online.dto.PerguntaResponseDTO;
import com.ana.pesquisa_online.dto.OpcaoResponseDTO;
import com.ana.pesquisa_online.entity.Pergunta;
import com.ana.pesquisa_online.entity.Pesquisa;
import com.ana.pesquisa_online.exception.ResourceNotFoundException;
import com.ana.pesquisa_online.repository.PerguntaRepository;
import com.ana.pesquisa_online.repository.PesquisaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerguntaService {

    private final PerguntaRepository perguntaRepository;
    private final PesquisaRepository pesquisaRepository;

    public PerguntaService(
            PerguntaRepository perguntaRepository,
            PesquisaRepository pesquisaRepository) {

        this.perguntaRepository = perguntaRepository;
        this.pesquisaRepository = pesquisaRepository;
    }

    // Converte Entidade -> DTO
    private PerguntaResponseDTO converterParaDTO(Pergunta pergunta) {

        PerguntaResponseDTO dto = new PerguntaResponseDTO();

        dto.setId(pergunta.getId());
        dto.setEnunciado(pergunta.getEnunciado());
        dto.setTipoPergunta(pergunta.getTipoPergunta());
        dto.setObrigatoria(pergunta.getObrigatoria());
        dto.setOrdem(pergunta.getOrdem());

        if (pergunta.getPesquisa() != null) {
            dto.setPesquisaId(pergunta.getPesquisa().getId());
        }

        List<OpcaoResponseDTO> opcoes = pergunta.getOpcoes()
                .stream()
                .map(opcao -> {

                    OpcaoResponseDTO opcaoDTO = new OpcaoResponseDTO();

                    opcaoDTO.setId(opcao.getId());
                    opcaoDTO.setDescricao(opcao.getDescricao());

                    if (opcao.getPergunta() != null) {
                        opcaoDTO.setPerguntaId(opcao.getPergunta().getId());
                    }

                    return opcaoDTO;
                })
                .toList();

        dto.setOpcoes(opcoes);

        return dto;
    }

    public List<PerguntaResponseDTO> listarTodas() {

        return perguntaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public PerguntaResponseDTO salvar(PerguntaRequestDTO dto) {

        Pergunta pergunta = new Pergunta();

        pergunta.setEnunciado(dto.getEnunciado());
        pergunta.setTipoPergunta(dto.getTipoPergunta());
        pergunta.setObrigatoria(dto.getObrigatoria());
        pergunta.setOrdem(dto.getOrdem());

        Pesquisa pesquisa = pesquisaRepository.findById(dto.getPesquisaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pesquisa não encontrada."));

        pergunta.setPesquisa(pesquisa);

        Pergunta salva = perguntaRepository.save(pergunta);

        return converterParaDTO(salva);
    }

    public PerguntaResponseDTO atualizar(Long id, PerguntaRequestDTO dto) {

        Pergunta pergunta = perguntaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pergunta não encontrada."));

        pergunta.setEnunciado(dto.getEnunciado());
        pergunta.setTipoPergunta(dto.getTipoPergunta());
        pergunta.setObrigatoria(dto.getObrigatoria());
        pergunta.setOrdem(dto.getOrdem());

        Pesquisa pesquisa = pesquisaRepository.findById(dto.getPesquisaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pesquisa não encontrada."));

        pergunta.setPesquisa(pesquisa);

        Pergunta atualizada = perguntaRepository.save(pergunta);

        return converterParaDTO(atualizada);
    }

    public void excluir(Long id) {

        Pergunta pergunta = perguntaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pergunta não encontrada."));

        perguntaRepository.delete(pergunta);
    }
}