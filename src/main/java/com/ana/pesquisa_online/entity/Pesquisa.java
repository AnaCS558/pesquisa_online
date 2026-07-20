package com.ana.pesquisa_online.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

    @Entity
    @Table(name = "pesquisa")
    public class Pesquisa {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_pesquisa")
        private Long id;

        @Column(nullable = false, length = 150)
        private String titulo;

        @Column(length = 500)
        private String descricao;

        @Column(nullable = false)
        private Boolean ativa = true;

        @Column(nullable = false)
        private LocalDateTime dataCriacao;

        private LocalDateTime dataEncerramento;

        @OneToMany(mappedBy = "pesquisa", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Pergunta> perguntas = new ArrayList<>();

        public Pesquisa() {
        }

        @PrePersist
        public void prePersist() {
            this.dataCriacao = LocalDateTime.now();
        }

        public Pesquisa(Long id, String titulo, String descricao, Boolean ativa, LocalDateTime dataCriacao, LocalDateTime dataEncerramento) {
            this.id = id;
            this.titulo = titulo;
            this.descricao = descricao;
            this.ativa = ativa;
            this.dataCriacao = dataCriacao;
            this.dataEncerramento = dataEncerramento;
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

        public List<Pergunta> getPerguntas() {
            return perguntas;
        }

        public void setPerguntas(List<Pergunta> perguntas) {
            this.perguntas = perguntas;
        }
    }

