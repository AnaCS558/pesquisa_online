package com.ana.pesquisa_online.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;

    @Entity
    @Table(name = "opcao")
    public class Opcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_opcao")
    private Long id;

    @NotBlank(message = "A descrição é obrigatória.")
    @Size(max = 200, message = "A descrição deve possuir no máximo 200 caracteres.")
    @Column(nullable = false, length = 200)
    private String descricao;

    @NotNull(message = "A opção deve pertencer a uma pergunta.")
    @ManyToOne
    @JoinColumn(name = "id_pergunta", nullable = false)
    @JsonBackReference("pergunta-opcao")
    private Pergunta pergunta;

    public Opcao() {
    }

    public Opcao(Long id, String descricao, Pergunta pergunta) {
        this.id = id;
        this.descricao = descricao;
        this.pergunta = pergunta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public void setPergunta(Pergunta pergunta) {
        this.pergunta = pergunta;
    }
}

