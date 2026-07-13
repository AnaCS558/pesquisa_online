package com.ana.pesquisa_online.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resposta")
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resposta")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_pergunta", nullable = false)
    private Pergunta pergunta;

    @ManyToOne
    @JoinColumn(name = "id_opcao")
    private Opcao opcao;

    @Column(columnDefinition = "TEXT")
    private String respostaTexto;

    @Column(nullable = false)
    private LocalDateTime dataResposta;

    public Resposta() {
    }

    @PrePersist
    public void prePersist() {
        this.dataResposta = LocalDateTime.now();
    }

    public Resposta(Long id, Usuario usuario, Pergunta pergunta, Opcao opcao, String respostaTexto, LocalDateTime dataResposta) {
        this.id = id;
        this.usuario = usuario;
        this.pergunta = pergunta;
        this.opcao = opcao;
        this.respostaTexto = respostaTexto;
        this.dataResposta = dataResposta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public void setPergunta(Pergunta pergunta) {
        this.pergunta = pergunta;
    }

    public Opcao getOpcao() {
        return opcao;
    }

    public void setOpcao(Opcao opcao) {
        this.opcao = opcao;
    }

    public String getRespostaTexto() {
        return respostaTexto;
    }

    public void setRespostaTexto(String respostaTexto) {
        this.respostaTexto = respostaTexto;
    }

    public LocalDateTime getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(LocalDateTime dataResposta) {
        this.dataResposta = dataResposta;
    }
}



