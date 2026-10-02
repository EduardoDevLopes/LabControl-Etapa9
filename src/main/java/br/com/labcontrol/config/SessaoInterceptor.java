package br.com.labcontrol.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Garante que as rotas protegidas (/api/**, exceto /api/auth/**) só
 * sejam acessadas por quem tem uma sessão de login ativa.
 *
 * Na Etapa 8, essa checagem era feita inteiramente no front-end
 * (LabControlDados.exigirSessao() em mock-data.js), o que é
 * facilmente contornável — qualquer um podia abrir o DevTools e
 * chamar as funções JS diretamente sem nunca ter feito login. Aqui a
 * checagem passa a ser feita no servidor, no HttpSession, então uma
 * chamada à API sem sessão válida recebe 401 mesmo que o JS do
 * cliente tenha sido adulterado ou ignorado.
 */
public class SessaoInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_SESSAO_USUARIO_ID = "usuarioId";
    public static final String ATRIBUTO_SESSAO_USUARIO_NOME = "usuarioNome";
    public static final String ATRIBUTO_SESSAO_USUARIO_PERFIL = "usuarioPerfil";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession sessao = request.getSession(false);

        if (sessao == null || sessao.getAttribute(ATRIBUTO_SESSAO_USUARIO_ID) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"status\":401,\"erro\":\"Sessão expirada ou inexistente\",\"mensagens\":[\"Faça login novamente.\"]}"
            );
            return false;
        }

        return true;
    }
}
