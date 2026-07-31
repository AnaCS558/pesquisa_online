package com.ana.pesquisa_online.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.ana.pesquisa_online.entity.enums.TipoPergunta;

    @Entity
    @Table(name = "pergunta")
    public class Pergunta {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_pergunta")
        private Long id;

        @NotBlank(message = "O enunciado é obrigatório.")
        @Size(max = 500, message = "O enunciado deve ter no máximo 500 caracteres.")
        @Column(nullable = false, length = 500)
        private String enunciado;

        @NotNull(message = "Informe o tipo da pergunta.")
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TipoPergunta tipoPergunta;

        @NotNull(message = "Informe se a pergunta é obrigatória.")
        @Column(nullable = false)
        private Boolean obrigatoria = true;

        @NotNull(message = "Informe a ordem da pergunta.")
        @Min(value = 1, message = "A ordem deve ser maior que zero.")
        @Column(nullable = false)
        private Integer ordem;

        @NotNull(message = "A pergunta deve pertencer a uma pesquisa.")
        @ManyToOne
        @JoinColumn(name = "id_pesquisa", nullable = false)
        @JsonBackReference("pesquisa-pergunta")
        private Pesquisa pesquisa;

        @OneToMany(mappedBy = "pergunta",
                cascade = CascadeType.ALL,
                orphanRemoval = true)
        private List<Opcao> opcoes = new ArrayList<>();

        @OneToMany(mappedBy = "pergunta",
                cascade = CascadeType.ALL,
                orphanRemoval = true)
        private List<Resposta> respostas = new ArrayList<>();

        public Pergunta() {
        }

        public Pergunta(Long id, String enunciado, TipoPergunta tipoPergunta, Boolean obrigatoria, Integer ordem, Pesquisa pesquisa) {
            this.id = id;
            this.enunciado = enunciado;
            this.tipoPergunta = tipoPergunta;
            this.obrigatoria = obrigatoria;
            this.ordem = ordem;
            this.pesquisa = pesquisa;
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

        public Pesquisa getPesquisa() {
            return pesquisa;
        }

        public void setPesquisa(Pesquisa pesquisa) {
            this.pesquisa = pesquisa;
        }

        public List<Opcao> getOpcoes() {
            return opcoes;
        }

        public void setOpcoes(List<Opcao> opcoes) {
            this.opcoes = opcoes;
        }

        public List<Resposta> getRespostas() {
            return respostas;
        }

        public void setRespostas(List<Resposta> respostas) {
            this.respostas = respostas;
        }
    }



