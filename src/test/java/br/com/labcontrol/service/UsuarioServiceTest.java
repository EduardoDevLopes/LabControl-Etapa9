package br.com.labcontrol.service;

import br.com.labcontrol.exception.AutenticacaoException;
import br.com.labcontrol.exception.RegistroNaoEncontradoException;
import br.com.labcontrol.exception.ValidacaoException;
import br.com.labcontrol.model.PerfilUsuario;
import br.com.labcontrol.model.Usuario;
import br.com.labcontrol.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários (JUnit 5) da camada de serviço de Usuario.
 *
 * Migrados da Etapa 7: o UsuarioDAOFake (em memória, implementando a
 * interface UsuarioDAO manual) foi substituído por um
 * UsuarioRepository mockado com Mockito, já que a persistência real
 * agora é feita via Spring Data JPA. Cada teste configura
 * explicitamente (via when(...).thenReturn(...)) o que o repository
 * "fingiria" encontrar no banco — tornando visível, teste a teste,
 * exatamente qual consulta o service dispara.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(usuarioRepository);
    }

    @Test
    @DisplayName("Cadastro de usuário válido deve receber id gerado")
    void cadastroDeUsuarioValidoDeveGerarId() {
        Usuario entrada = new Usuario("Maria Silva", "maria@lab.com", PerfilUsuario.OPERADOR, "maria", "123456");

        when(usuarioRepository.findByLogin("maria")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(chamada -> {
            Usuario salvo = chamada.getArgument(0);
            salvo.setId(1);
            return salvo;
        });

        Usuario salvo = service.cadastrar(entrada);
        assertTrue(salvo.getId() > 0);
    }

    @Test
    @DisplayName("Cadastro com nome vazio deve lançar ValidacaoException")
    void cadastroComNomeVazioDeveFalhar() {
        Usuario usuario = new Usuario("", "x@lab.com", PerfilUsuario.OPERADOR, "x", "123456");
        assertThrows(ValidacaoException.class, () -> service.cadastrar(usuario));
    }

    @Test
    @DisplayName("Login duplicado não deve ser permitido")
    void loginDuplicadoDeveFalhar() {
        Usuario existente = new Usuario("Usuário 1", "u1@lab.com", PerfilUsuario.OPERADOR, "duplicado", "123456");
        existente.setId(1);

        when(usuarioRepository.findByLogin("duplicado")).thenReturn(Optional.of(existente));

        Usuario segundo = new Usuario("Usuário 2", "u2@lab.com", PerfilUsuario.OPERADOR, "duplicado", "123456");
        assertThrows(ValidacaoException.class, () -> service.cadastrar(segundo));
    }

    @Test
    @DisplayName("Autenticação com credenciais corretas deve retornar o usuário")
    void autenticacaoComSucesso() {
        Usuario admin = new Usuario("Admin", "admin@lab.com", PerfilUsuario.ADMINISTRADOR, "admin", "123456");
        admin.setId(1);

        when(usuarioRepository.findByLoginAndSenha("admin", "123456")).thenReturn(Optional.of(admin));

        Usuario autenticado = service.autenticar("admin", "123456");
        assertEquals(PerfilUsuario.ADMINISTRADOR, autenticado.getPerfil());
    }

    @Test
    @DisplayName("Autenticação com senha incorreta deve lançar AutenticacaoException")
    void autenticacaoComSenhaIncorretaDeveFalhar() {
        when(usuarioRepository.findByLoginAndSenha("admin", "senha-errada")).thenReturn(Optional.empty());
        assertThrows(AutenticacaoException.class, () -> service.autenticar("admin", "senha-errada"));
    }

    @Test
    @DisplayName("Atualização de usuário inexistente deve lançar RegistroNaoEncontradoException")
    void atualizacaoDeUsuarioInexistenteDeveFalhar() {
        Usuario usuario = new Usuario("Fantasma", "fantasma@lab.com", PerfilUsuario.OPERADOR, "fantasma", "123456");
        usuario.setId(999);

        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RegistroNaoEncontradoException.class, () -> service.atualizar(usuario));
    }
}
