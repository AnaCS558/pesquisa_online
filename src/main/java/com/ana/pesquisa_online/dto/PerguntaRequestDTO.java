package com.ana.pesquisa_online.dto;

import com.ana.pesquisa_online.entity.enums.TipoPergunta;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PerguntaRequestDTO {
    @NotBlank(message = "O enunciado é obrigatório.")
    @Size(max = 500, message = "O enunciado deve ter no máximo 500 caracteres.")
    private String enunciado;

    @NotNull(message = "Informe o tipo da pergunta.")
    private TipoPergunta tipoPergunta;

    @NotNull(message = "Informe se a pergunta é obrigatória.")
    private Boolean obrigatoria = true;

    @NotNull(message = "Informe a ordem da pergunta.")
    @Min(value = 1, message = "A ordem deve ser maior que zero.")
    private Integer ordem;

    @NotNull(message = "Informe a pesquisa.")
    private Long pesquisaId;

    public PerguntaRequestDTO() {
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
}
