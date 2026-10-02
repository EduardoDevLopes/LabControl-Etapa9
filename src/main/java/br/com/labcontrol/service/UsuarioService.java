package br.com.labcontrol.service;

import br.com.labcontrol.exception.AutenticacaoException;
import br.com.labcontrol.exception.RegistroNaoEncontradoException;
import br.com.labcontrol.exception.ValidacaoException;
import br.com.labcontrol.model.Usuario;
import br.com.labcontrol.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Regras de negócio relacionadas a Usuario.
 *
 * Migrada da Etapa 6/7 preservando literalmente as mesmas validações
 * (cobertas por UsuarioServiceTest, com 6 testes JUnit passando).
 * A única mudança estrutural é a troca da dependência de UsuarioDAO
 * (interface JDBC manual) por UsuarioRepository (Spring Data JPA);
 * nenhuma regra de negócio foi alterada.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastrar(Usuario usuario) {
        validarCamposObrigatorios(usuario);
        validarLoginDisponivel(usuario.getLogin(), null);
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Usuario usuario) {
        validarCamposObrigatorios(usuario);
        Usuario existente = buscarPorIdOuFalhar(usuario.getId());
        validarLoginDisponivel(usuario.getLogin(), usuario.getId());

        existente.setNome(usuario.getNome());
        existente.setEmail(usuario.getEmail());
        existente.setLogin(usuario.getLogin());
        existente.setPerfil(usuario.getPerfil());
        // Senha só é alterada se uma nova foi explicitamente enviada —
        // mesma regra já aplicada no formulário da Etapa 8
        // (usuario-form.js: "Deixe em branco para manter a senha atual").
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            existente.setSenha(usuario.getSenha());
        }

        return usuarioRepository.save(existente);
    }

    public void excluir(int id) {
        buscarPorIdOuFalhar(id);
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorIdOuFalhar(int id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RegistroNaoEncontradoException("Usuário não encontrado (id=" + id + ")."));
    }

    /**
     * Autentica um usuário pelo par login/senha.
     */
    public Usuario autenticar(String login, String senha) {
        if (isVazio(login) || isVazio(senha)) {
            throw new ValidacaoException("Informe usuário e senha.");
        }

        Optional<Usuario> usuario = usuarioRepository.findByLoginAndSenha(login, senha);
        return usuario.orElseThrow(() -> new AutenticacaoException("Usuário ou senha inválidos."));
    }

    private void validarCamposObrigatorios(Usuario usuario) {
        if (usuario == null) {
            throw new ValidacaoException("Usuário não pode ser nulo.");
        }
        if (isVazio(usuario.getNome())) {
            throw new ValidacaoException("O nome é obrigatório.");
        }
        if (isVazio(usuario.getEmail())) {
            throw new ValidacaoException("O e-mail é obrigatório.");
        }
        if (isVazio(usuario.getLogin())) {
            throw new ValidacaoException("O login é obrigatório.");
        }
        // Na atualização a senha pode vir vazia (mantém a atual); no
        // cadastro, o controller garante que uma senha foi enviada
        // antes mesmo de chegar aqui (ver UsuarioApiController).
        if (usuario.getPerfil() == null) {
            throw new ValidacaoException("O perfil é obrigatório.");
        }
    }

    /**
     * Garante que o login é único.
     */
    private void validarLoginDisponivel(String login, Integer idIgnorado) {
        Optional<Usuario> existente = usuarioRepository.findByLogin(login);
        boolean loginEmUsoPorOutroUsuario = existente.isPresent()
            && (idIgnorado == null || !existente.get().getId().equals(idIgnorado));

        if (loginEmUsoPorOutroUsuario) {
            throw new ValidacaoException("Já existe um usuário com o login '" + login + "'.");
        }
    }

    private boolean isVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
