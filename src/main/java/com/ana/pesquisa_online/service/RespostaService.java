package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.dto.RespostaRequestDTO;
import com.ana.pesquisa_online.dto.RespostaResponseDTO;

import com.ana.pesquisa_online.entity.Resposta;
import com.ana.pesquisa_online.entity.enums.TipoPergunta;

import com.ana.pesquisa_online.exception.BusinessRuleException;
import com.ana.pesquisa_online.exception.ResourceNotFoundException;

import com.ana.pesquisa_online.repository.OpcaoRepository;
import com.ana.pesquisa_online.repository.PerguntaRepository;
import com.ana.pesquisa_online.repository.RespostaRepository;
import com.ana.pesquisa_online.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RespostaService {

    private final RespostaRepository respostaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerguntaRepository perguntaRepository;
    private final OpcaoRepository opcaoRepository;

    public RespostaService(
            RespostaRepository respostaRepository,
            UsuarioRepository usuarioRepository,
            PerguntaRepository perguntaRepository,
            OpcaoRepository opcaoRepository) {

        this.respostaRepository = respostaRepository;
        this.usuarioRepository = usuarioRepository;
        this.perguntaRepository = perguntaRepository;
        this.opcaoRepository = opcaoRepository;
    }

    private RespostaResponseDTO converterParaDTO(Resposta resposta) {

        RespostaResponseDTO dto = new RespostaResponseDTO();

        dto.setId(resposta.getId());
        dto.setUsuarioId(resposta.getUsuario().getId());
        dto.setPerguntaId(resposta.getPergunta().getId());

        if (resposta.getOpcao() != null) {
            dto.setOpcaoId(resposta.getOpcao().getId());
        }

        dto.setRespostaTexto(resposta.getRespostaTexto());
        dto.setDataResposta(resposta.getDataResposta());

        return dto;
    }

    public List<RespostaResponseDTO> listarTodas() {

        return respostaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public RespostaResponseDTO salvar(RespostaRequestDTO dto) {

        Resposta resposta = new Resposta();

        resposta.setUsuario(
                usuarioRepository.findById(dto.getUsuarioId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Usuário não encontrado."))
        );

        resposta.setPergunta(
                perguntaRepository.findById(dto.getPerguntaId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Pergunta não encontrada."))
        );

        if (dto.getOpcaoId() != null) {
            resposta.setOpcao(
                    opcaoRepository.findById(dto.getOpcaoId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Opção não encontrada."))
            );
        }

        resposta.setRespostaTexto(dto.getRespostaTexto());

        validarResposta(resposta);

        return converterParaDTO(respostaRepository.save(resposta));
    }

    private void validarResposta(Resposta resposta) {

        if (!resposta.getPergunta().getPesquisa().getAtiva()) {
            throw new BusinessRuleException("A pesquisa está inativa.");
        }

        if (resposta.getPergunta().getPesquisa().getDataEncerramento() != null
                && resposta.getPergunta().getPesquisa()
                .getDataEncerramento()
                .isBefore(LocalDateTime.now())) {

            throw new BusinessRuleException("A pesquisa já foi encerrada.");
        }

        TipoPergunta tipo = resposta.getPergunta().getTipoPergunta();

        if (resposta.getOpcao() != null) {

            boolean opcaoPertence = resposta.getPergunta()
                    .getOpcoes()
                    .stream()
                    .anyMatch(opcao ->
                            opcao.getId().equals(resposta.getOpcao().getId()));

            if (!opcaoPertence) {
                throw new BusinessRuleException(
                        "A opção selecionada não pertence à pergunta."
                );
            }
        }

        if (tipo == TipoPergunta.Aberta) {

            if (resposta.getRespostaTexto() == null
                    || resposta.getRespostaTexto().isBlank()) {

                throw new BusinessRuleException(
                        "A resposta de texto é obrigatória para perguntas abertas."
                );
            }

            if (resposta.getOpcao() != null) {
                throw new BusinessRuleException(
                        "Perguntas abertas não devem possuir uma opção."
                );
            }

        } else {

            if (resposta.getOpcao() == null) {
                throw new BusinessRuleException(
                        "Informe uma opção para esta pergunta."
                );
            }

            if (resposta.getRespostaTexto() != null
                    && !resposta.getRespostaTexto().isBlank()) {

                throw new BusinessRuleException(
                        "Perguntas de escolha não devem possuir resposta de texto."
                );
            }
        }
    }

    public RespostaResponseDTO atualizar(Long id, RespostaRequestDTO dto) {

        Resposta resposta = respostaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resposta não encontrada."));

        resposta.setUsuario(
                usuarioRepository.findById(dto.getUsuarioId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Usuário não encontrado."))
        );

        resposta.setPergunta(
                perguntaRepository.findById(dto.getPerguntaId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Pergunta não encontrada."))
        );

        if (dto.getOpcaoId() != null) {
            resposta.setOpcao(
                    opcaoRepository.findById(dto.getOpcaoId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Opção não encontrada."))
            );
        } else {
            resposta.setOpcao(null);
        }

        resposta.setRespostaTexto(dto.getRespostaTexto());

        validarResposta(resposta);

        return converterParaDTO(respostaRepository.save(resposta));
    }

    public void excluir(Long id) {

        Resposta resposta = respostaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resposta não encontrada."));

        respostaRepository.delete(resposta);
    }
}