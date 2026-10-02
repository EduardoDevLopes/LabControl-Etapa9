package br.com.labcontrol.controller;

import br.com.labcontrol.config.SessaoInterceptor;
import br.com.labcontrol.dto.ErroRespostaDTO;
import br.com.labcontrol.dto.UsuarioDTO;
import br.com.labcontrol.model.Usuario;
import br.com.labcontrol.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Autenticação da aplicação: login, logout e consulta da sessão
 * atual. Chamado por login.js (adaptado da Etapa 8) via fetch().
 *
 * Não é protegido por SessaoInterceptor (ver WebConfig), já que o
 * próprio login precisa funcionar sem sessão ainda existir.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> corpo, HttpServletRequest request) {
        String login = corpo.get("login");
        String senha = corpo.get("senha");

        Usuario usuario = usuarioService.autenticar(login, senha);

        HttpSession sessao = request.getSession(true);
        sessao.setAttribute(SessaoInterceptor.ATRIBUTO_SESSAO_USUARIO_ID, usuario.getId());
        sessao.setAttribute(SessaoInterceptor.ATRIBUTO_SESSAO_USUARIO_NOME, usuario.getNome());
        sessao.setAttribute(SessaoInterceptor.ATRIBUTO_SESSAO_USUARIO_PERFIL, usuario.getPerfil().name());

        return ResponseEntity.ok(UsuarioDTO.deEntidade(usuario));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessao")
    public ResponseEntity<?> sessaoAtual(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);

        if (sessao == null || sessao.getAttribute(SessaoInterceptor.ATRIBUTO_SESSAO_USUARIO_ID) == null) {
            ErroRespostaDTO corpo = new ErroRespostaDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Sem sessão ativa",
                List.of("Nenhum usuário logado.")
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(corpo);
        }

        Integer id = (Integer) sessao.getAttribute(SessaoInterceptor.ATRIBUTO_SESSAO_USUARIO_ID);
        Usuario usuario = usuarioService.buscarPorIdOuFalhar(id);

        return ResponseEntity.ok(UsuarioDTO.deEntidade(usuario));
    }
}
