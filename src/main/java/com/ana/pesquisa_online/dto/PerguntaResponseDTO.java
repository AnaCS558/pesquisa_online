package com.ana.pesquisa_online.dto;

import com.ana.pesquisa_online.entity.enums.TipoPergunta;

import java.util.List;

public class PerguntaResponseDTO {

    private Long id;
    private String enunciado;
    private TipoPergunta tipoPergunta;
    private Boolean obrigatoria;
    private Integer ordem;
    private Long pesquisaId;
    private List<OpcaoResponseDTO> opcoes;

    public PerguntaResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public TipoPergunta getTipoPergunta() {
        return tipoPergunta;
    }

    public void setTipoPergunta(TipoPergunta tipoPergunta) {
        this.tipoPergunta = tipoPergunta;
    }

    public Boolean getObrigatoria() {
        return obrigatoria;
    }

    public void setObrigatoria(Boolean obrigatoria) {
        this.obrigatoria = obrigatoria;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Long getPesquisaId() {
        return pesquisaId;
    }

    public void setPesquisaId(Long pesquisaId) {
        this.pesquisaId = pesquisaId;
    }

    public List<OpcaoResponseDTO> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(List<OpcaoResponseDTO> opcoes) {
        this.opcoes = opcoes;
    }
}
