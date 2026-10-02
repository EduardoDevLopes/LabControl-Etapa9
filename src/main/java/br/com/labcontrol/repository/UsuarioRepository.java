package br.com.labcontrol.repository;

import br.com.labcontrol.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório Spring Data JPA para Usuario.
 *
 * Substitui UsuarioDAO/UsuarioDAOJdbc da Etapa 6, que continham SQL
 * manual (PreparedStatement, ResultSet) escrito à mão para cada
 * operação. Aqui, o Spring Data gera as implementações em tempo de
 * execução a partir das assinaturas dos métodos — findByLogin, por
 * exemplo, vira automaticamente
 * "SELECT * FROM usuarios WHERE login = ?".
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLogin(String login);

    Optional<Usuario> findByLoginAndSenha(String login, String senha);
}
