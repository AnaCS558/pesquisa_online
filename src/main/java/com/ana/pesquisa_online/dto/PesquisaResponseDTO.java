package com.ana.pesquisa_online.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PesquisaResponseDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private Boolean ativa;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEncerramento;
    private List<PerguntaResponseDTO> perguntas;

    public PesquisaResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDateTime dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public List<PerguntaResponseDTO> getPerguntas() {
        return perguntas;
    }

    public void setPerguntas(List<PerguntaResponseDTO> perguntas) {
        this.perguntas = perguntas;
    }
}
