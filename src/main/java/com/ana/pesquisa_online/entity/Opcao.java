package com.ana.pesquisa_online.entity;

import jakarta.persistence.*;

    @Entity
    @Table(name = "opcao")
    public class Opcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_opcao")
    private Long id;

    @Column(nullable = false, length = 200)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "id_pergunta", nullable = false)
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

