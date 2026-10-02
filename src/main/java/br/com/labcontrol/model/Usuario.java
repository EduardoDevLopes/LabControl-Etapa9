package br.com.labcontrol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa um usuário do sistema.
 *
 * Mapeia diretamente a tabela 'usuarios' já criada nas Etapas 6/7
 * (UC_15_Etapa6.sql), sem alteração de estrutura. Na versão desktop
 * (Etapa 6), esta classe era um POJO simples persistido via JDBC
 * manual (UsuarioDAOJdbc); aqui, as mesmas colunas são mapeadas via
 * anotações JPA e a persistência passa a ser feita por
 * UsuarioRepository (Spring Data JPA), eliminando o SQL manual.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PerfilUsuario perfil;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false, length = 100)
    private String senha;

    public Usuario() {
    }

    public Usuario(String nome, String email, PerfilUsuario perfil, String login, String senha) {
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.login = login;
        this.senha = senha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', login='" + login + "', perfil=" + perfil + "}";
    }
}
