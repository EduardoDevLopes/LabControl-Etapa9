package br.com.labcontrol.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serve os templates Thymeleaf (o "shell" HTML de cada página).
 *
 * Segue o mesmo padrão adotado no projeto Casa Cultural Cinema
 * (Atividade 3): este controller NUNCA acessa repositórios ou
 * services de dados — sua única responsabilidade é devolver qual
 * template renderizar. Toda a busca, validação e persistência de
 * dados acontece via chamadas AJAX dos controllers REST
 * (AuthController, UsuarioApiController, EquipamentoApiController),
 * feitas pelo JavaScript de cada página depois que o HTML carrega.
 *
 * Não há checagem de sessão aqui: um usuário deslogado consegue abrir
 * a URL de dashboard.html, mas o próprio JS da página (layout.js)
 * chama /api/auth/sessao ao carregar e redireciona para o login se a
 * chamada retornar 401 — a mesma janela de tempo que já existia
 * na Etapa 8, agora reforçada pelo SessaoInterceptor no back-end
 * (que bloqueia as chamadas de API mesmo que o redirecionamento no
 * JS seja contornado).
 */
@Controller
public class PaginaController {

    @GetMapping("/")
    public String raiz() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios() {
        return "usuarios";
    }

    @GetMapping("/usuarios/novo")
    public String usuarioNovo() {
        return "usuario-form";
    }

    @GetMapping("/usuarios/{id}/editar")
    public String usuarioEditar() {
        return "usuario-form";
    }

    @GetMapping("/equipamentos")
    public String equipamentos() {
        return "equipamentos";
    }

    @GetMapping("/equipamentos/novo")
    public String equipamentoNovo() {
        return "equipamento-form";
    }

    @GetMapping("/equipamentos/{id}/editar")
    public String equipamentoEditar() {
        return "equipamento-form";
    }
}
