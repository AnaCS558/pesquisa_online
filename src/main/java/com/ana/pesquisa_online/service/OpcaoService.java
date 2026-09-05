package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.dto.OpcaoRequestDTO;
import com.ana.pesquisa_online.dto.OpcaoResponseDTO;
import com.ana.pesquisa_online.entity.Opcao;
import com.ana.pesquisa_online.entity.Pergunta;
import com.ana.pesquisa_online.exception.ResourceNotFoundException;
import com.ana.pesquisa_online.repository.OpcaoRepository;
import com.ana.pesquisa_online.repository.PerguntaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpcaoService {

    private final OpcaoRepository opcaoRepository;
    private final PerguntaRepository perguntaRepository;

    public OpcaoService(
            OpcaoRepository opcaoRepository,
            PerguntaRepository perguntaRepository) {

        this.opcaoRepository = opcaoRepository;
        this.perguntaRepository = perguntaRepository;
    }

    // Converte Entidade -> DTO
    private OpcaoResponseDTO converterParaDTO(Opcao opcao) {

        OpcaoResponseDTO dto = new OpcaoResponseDTO();

        dto.setId(opcao.getId());
        dto.setDescricao(opcao.getDescricao());

        if (opcao.getPergunta() != null) {
            dto.setPerguntaId(opcao.getPergunta().getId());
        }

        return dto;
    }

    public List<OpcaoResponseDTO> listarTodas() {

        return opcaoRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public OpcaoResponseDTO buscarPorId(Long id) {

        Opcao opcao = opcaoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Opção não encontrada."));

        return converterParaDTO(opcao);
    }

    public OpcaoResponseDTO salvar(OpcaoRequestDTO dto) {

        Opcao opcao = new Opcao();

        opcao.setDescricao(dto.getDescricao());

        Pergunta pergunta = perguntaRepository.findById(dto.getPerguntaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pergunta não encontrada."));

        opcao.setPergunta(pergunta);

        Opcao salva = opcaoRepository.save(opcao);

        return converterParaDTO(salva);
    }

    public OpcaoResponseDTO atualizar(Long id, OpcaoRequestDTO dto) {

        Opcao opcao = opcaoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Opção não encontrada."));

        opcao.setDescricao(dto.getDescricao());

        Pergunta pergunta = perguntaRepository.findById(dto.getPerguntaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pergunta não encontrada."));

        opcao.setPergunta(pergunta);

        Opcao atualizada = opcaoRepository.save(opcao);

        return converterParaDTO(atualizada);
    }

    public void excluir(Long id) {

        Opcao opcao = opcaoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Opção não encontrada."));

        opcaoRepository.delete(opcao);
    }
}