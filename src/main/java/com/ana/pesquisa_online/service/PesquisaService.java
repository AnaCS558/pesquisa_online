package com.ana.pesquisa_online.service;

import com.ana.pesquisa_online.dto.OpcaoResponseDTO;
import com.ana.pesquisa_online.dto.PerguntaResponseDTO;
import com.ana.pesquisa_online.dto.PesquisaRequestDTO;
import com.ana.pesquisa_online.dto.PesquisaResponseDTO;
import com.ana.pesquisa_online.entity.Pesquisa;
import com.ana.pesquisa_online.exception.ResourceNotFoundException;
import com.ana.pesquisa_online.repository.PesquisaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PesquisaService {

    private final PesquisaRepository pesquisaRepository;

    public PesquisaService(PesquisaRepository pesquisaRepository) {
        this.pesquisaRepository = pesquisaRepository;
    }

    // Converte Entidade -> DTO
    private PesquisaResponseDTO converterParaDTO(Pesquisa pesquisa) {

        PesquisaResponseDTO dto = new PesquisaResponseDTO();

        dto.setId(pesquisa.getId());
        dto.setTitulo(pesquisa.getTitulo());
        dto.setDescricao(pesquisa.getDescricao());
        dto.setAtiva(pesquisa.getAtiva());
        dto.setDataCriacao(pesquisa.getDataCriacao());
        dto.setDataEncerramento(pesquisa.getDataEncerramento());

        List<PerguntaResponseDTO> perguntas = pesquisa.getPerguntas()
                .stream()
                .map(pergunta -> {

                    PerguntaResponseDTO perguntaDTO = new PerguntaResponseDTO();

                    perguntaDTO.setId(pergunta.getId());
                    perguntaDTO.setEnunciado(pergunta.getEnunciado());
                    perguntaDTO.setTipoPergunta(pergunta.getTipoPergunta());
                    perguntaDTO.setObrigatoria(pergunta.getObrigatoria());
                    perguntaDTO.setOrdem(pergunta.getOrdem());

                    if (pergunta.getPesquisa() != null) {
                        perguntaDTO.setPesquisaId(
                                pergunta.getPesquisa().getId()
                        );
                    }

                    List<OpcaoResponseDTO> opcoes = pergunta.getOpcoes()
                            .stream()
                            .map(opcao -> {

                                OpcaoResponseDTO opcaoDTO =
                                        new OpcaoResponseDTO();

                                opcaoDTO.setId(opcao.getId());
                                opcaoDTO.setDescricao(opcao.getDescricao());

                                if (opcao.getPergunta() != null) {
                                    opcaoDTO.setPerguntaId(
                                            opcao.getPergunta().getId()
                                    );
                                }

                                return opcaoDTO;
                            })
                            .toList();

                    perguntaDTO.setOpcoes(opcoes);

                    return perguntaDTO;
                })
                .toList();

        dto.setPerguntas(perguntas);

        return dto;
    }

    public List<PesquisaResponseDTO> listarTodas() {

        return pesquisaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public PesquisaResponseDTO buscarPorId(Long id) {

        Pesquisa pesquisa = pesquisaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pesquisa não encontrada."
                        ));

        return converterParaDTO(pesquisa);
    }

    public PesquisaResponseDTO salvar(PesquisaRequestDTO dto) {

        Pesquisa pesquisa = new Pesquisa();

        pesquisa.setTitulo(dto.getTitulo());
        pesquisa.setDescricao(dto.getDescricao());
        pesquisa.setAtiva(dto.getAtiva());
        pesquisa.setDataEncerramento(dto.getDataEncerramento());

        Pesquisa salva = pesquisaRepository.save(pesquisa);

        return converterParaDTO(salva);
    }

    public PesquisaResponseDTO atualizar(
            Long id,
            PesquisaRequestDTO dto) {

        Pesquisa pesquisa = pesquisaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pesquisa não encontrada."
                        ));

        pesquisa.setTitulo(dto.getTitulo());
        pesquisa.setDescricao(dto.getDescricao());
        pesquisa.setAtiva(dto.getAtiva());
        pesquisa.setDataEncerramento(dto.getDataEncerramento());

        Pesquisa atualizada = pesquisaRepository.save(pesquisa);

        return converterParaDTO(atualizada);
    }

    public void excluir(Long id) {

        if (!pesquisaRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Pesquisa não encontrada."
            );
        }

        pesquisaRepository.deleteById(id);
    }
}