/**
 * LabControl — Layout compartilhado (sidebar + topo)
 *
 * Adaptado da Etapa 8 para o back-end Spring: os links agora apontam
 * para as rotas do PaginaController (/dashboard, /usuarios,
 * /equipamentos — sem ".html"), e iniciarPagina() passa a ser
 * assíncrona porque exigirSessao() consulta o servidor
 * (GET /api/auth/sessao) em vez de ler sessionStorage local.
 */

const LabControlLayout = (() => {
  function montarSidebar(paginaAtiva) {
    const links = [
      { chave: "dashboard", href: "/dashboard", rotulo: "Dashboard" },
      { chave: "usuarios", href: "/usuarios", rotulo: "Usuários" },
      { chave: "equipamentos", href: "/equipamentos", rotulo: "Equipamentos" },
    ];

    const itensHtml = links
      .map((link) => {
        const classeAtiva = link.chave === paginaAtiva ? " ativo" : "";
        return `<a class="sidebar__link${classeAtiva}" href="${link.href}">${link.rotulo}</a>`;
      })
      .join("\n");

    return `
      <aside class="sidebar">
        <div class="sidebar__marca">
          <span class="sidebar__marca-icone">◈</span>
          <span class="sidebar__marca-nome">LabControl</span>
        </div>
        <nav class="sidebar__nav">
          ${itensHtml}
        </nav>
        <div class="sidebar__rodape">
          <button type="button" class="sidebar__sair" id="botao-sair">Sair</button>
        </div>
      </aside>
    `;
  }

  function montarTopo(sessao) {
    const rotuloPerfil = LabControlApi.PERFIL_ROTULO[sessao.perfil] || sessao.perfil;
    return `
      <header class="topo">
        <div></div>
        <div class="topo__usuario">
          <span class="topo__usuario-nome">${sessao.nome}</span>
          <span class="topo__usuario-perfil">${rotuloPerfil}</span>
        </div>
      </header>
    `;
  }

  /**
   * Monta o esqueleto completo (sidebar + topo) dentro do elemento
   * #app, exige sessão ativa (validada no servidor) e devolve a
   * sessão para a página usar. A página deve conter
   * <div id="app"><main class="pagina" id="conteudo-pagina">...</main></div>
   */
  async function iniciarPagina(paginaAtiva) {
    const sessao = await LabControlApi.exigirSessao();
    if (!sessao) return null;

    const app = document.getElementById("app");
    const conteudoOriginal = document.getElementById("conteudo-pagina");
    const conteudoHtml = conteudoOriginal ? conteudoOriginal.outerHTML : "";

    app.innerHTML = `
      ${montarSidebar(paginaAtiva)}
      <div class="conteudo">
        ${montarTopo(sessao)}
        ${conteudoHtml}
      </div>
    `;

    document.getElementById("botao-sair").addEventListener("click", async () => {
      await LabControlApi.encerrarSessao();
      window.location.href = "/login";
    });

    return sessao;
  }

  return { iniciarPagina };
})();
