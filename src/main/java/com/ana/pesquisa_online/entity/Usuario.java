package com.ana.pesquisa_online.entity;

import com.ana.pesquisa_online.entity.enums.PerfilUsuario;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

    @Entity
    @Table(name = "usuario")
    public class Usuario {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_usuario")
        private Long id;

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres.")
        @Column(nullable = false, length = 120)
        private String nome;

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        @Column(nullable = false, unique = true, length = 150)
        private String email;

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve possuir pelo menos 6 caracteres.")
        @Column(nullable = false)
        private String senha;

        @NotNull(message = "Informe o perfil do usuário.")
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PerfilUsuario perfil;

        @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Resposta> respostas = new ArrayList<>();

        public Usuario() {
        }

        public Usuario(Long id, String nome, String email, String senha, PerfilUsuario perfil) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.senha = senha;
            this.perfil = perfil;
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

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }

        public PerfilUsuario getPerfil() {
            return perfil;
        }

        public void setPerfil(PerfilUsuario perfil) {
            this.perfil = perfil;
        }

        public List<Resposta> getRespostas() {
            return respostas;
        }

        public void setRespostas(List<Resposta> respostas) {
            this.respostas = respostas;
        }
    }
