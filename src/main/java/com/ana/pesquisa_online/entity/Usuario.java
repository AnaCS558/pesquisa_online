package com.ana.pesquisa_online.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

    @Entity
    @Table(name = "Participante")
    public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participante")
    private Long id;

    @Column(length = 120)
    private String nome;

    @Column(length = 150)
    private String email;

    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL)
    private List<Resposta> respostas = new ArrayList<>();

    public Participante() {
    }

    public Participante(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Resposta> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<Resposta> respostas) {
        this.respostas = respostas;
    }
}
