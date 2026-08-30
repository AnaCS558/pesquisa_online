package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.entity.Resposta;
import com.ana.pesquisa_online.entity.enums.TipoPergunta;
import com.ana.pesquisa_online.exception.BusinessRuleException;
import com.ana.pesquisa_online.exception.ResourceNotFoundException;
import com.ana.pesquisa_online.repository.RespostaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resposta não encontrada."));
    }

    public Resposta salvar(Resposta resposta) {

        validarResposta(resposta);

        return respostaRepository.save(resposta);
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

    public Resposta atualizar(Long id, Resposta respostaAtualizada) {

        Resposta resposta = respostaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resposta não encontrada."));

        resposta.setUsuario(respostaAtualizada.getUsuario());
        resposta.setPergunta(respostaAtualizada.getPergunta());
        resposta.setOpcao(respostaAtualizada.getOpcao());
        resposta.setRespostaTexto(respostaAtualizada.getRespostaTexto());

        validarResposta(resposta);

        return respostaRepository.save(resposta);
    }

    public void excluir(Long id) {

        Resposta resposta = buscarPorId(id);

        respostaRepository.delete(resposta);
    }
}