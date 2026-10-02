package br.com.labcontrol.dto;

import br.com.labcontrol.model.PerfilUsuario;
import br.com.labcontrol.model.Usuario;

/**
 * DTO de Usuario para a API REST.
 *
 * Existe por um motivo concreto de segurança: se a entidade Usuario
 * fosse serializada diretamente pelo Jackson, o campo 'senha' iria
 * junto em toda resposta JSON (inclusive na listagem de usuários,
 * visível a qualquer perfil autenticado). O DTO nunca inclui a senha
 * nas respostas; no campo de entrada (para cadastro/edição), a senha
 * é aceita normalmente e tratada pelo service.
 */
public class UsuarioDTO {

    private Integer id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;
    private String login;
    private String senha;

    public UsuarioDTO() {
    }

    public static UsuarioDTO deEntidade(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.id = usuario.getId();
        dto.nome = usuario.getNome();
        dto.email = usuario.getEmail();
        dto.perfil = usuario.getPerfil();
        dto.login = usuario.getLogin();
        // 'senha' propositalmente não é copiada — nunca sai em uma resposta.
        return dto;
    }

    public Usuario paraEntidade() {
        Usuario usuario = new Usuario();
        usuario.setId(this.id);
        usuario.setNome(this.nome);
        usuario.setEmail(this.email);
        usuario.setPerfil(this.perfil);
        usuario.setLogin(this.login);
        usuario.setSenha(this.senha);
        return usuario;
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
}
