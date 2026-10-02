/**
 * LabControl — Listagem de usuários
 * Busca por nome/login e exclusão com confirmação, agora via API
 * REST (dados persistidos em MySQL, não mais sessionStorage).
 */

document.addEventListener("DOMContentLoaded", async () => {
  const sessao = await LabControlLayout.iniciarPagina("usuarios");
  if (!sessao) return;

  const corpoTabela = document.getElementById("corpo-tabela-usuarios");
  const mensagemSemResultados = document.getElementById("mensagem-sem-resultados");
  const campoBusca = document.getElementById("campo-busca");
  const modalExclusao = document.getElementById("modal-exclusao");
  const textoModal = document.getElementById("modal-exclusao-texto");
  const botaoCancelar = document.getElementById("botao-cancelar-exclusao");
  const botaoConfirmar = document.getElementById("botao-confirmar-exclusao");

  let idParaExcluir = null;
  let todosUsuarios = [];

  function filtrar(usuarios, termo) {
    const termoNormalizado = (termo || "").trim().toLowerCase();
    if (!termoNormalizado) return usuarios;
    return usuarios.filter(
      (usuario) =>
        usuario.nome.toLowerCase().includes(termoNormalizado) ||
        usuario.login.toLowerCase().includes(termoNormalizado)
    );
  }

  function renderizar(termo) {
    const usuarios = filtrar(todosUsuarios, termo);

    if (usuarios.length === 0) {
      corpoTabela.innerHTML = "";
      mensagemSemResultados.hidden = false;
      return;
    }

    mensagemSemResultados.hidden = true;
    corpoTabela.innerHTML = usuarios
      .map((usuario) => {
        const rotuloPerfil = LabControlApi.PERFIL_ROTULO[usuario.perfil] || usuario.perfil;
        return `
          <tr>
            <td class="celula-principal">${usuario.nome}</td>
            <td>${usuario.login}</td>
            <td>${rotuloPerfil}</td>
            <td class="coluna-acoes">
              <a class="btn btn-secundario" href="/usuarios/${usuario.id}/editar">Editar</a>
              <button type="button" class="btn btn-perigo" data-excluir="${usuario.id}" data-nome="${usuario.nome}">Excluir</button>
            </td>
          </tr>
        `;
      })
      .join("");
  }

  async function carregar() {
    todosUsuarios = await LabControlApi.listarUsuarios();
    renderizar(campoBusca.value);
  }

  campoBusca.addEventListener("input", (evento) => renderizar(evento.target.value));

  corpoTabela.addEventListener("click", (evento) => {
    const botao = evento.target.closest("[data-excluir]");
    if (!botao) return;

    idParaExcluir = botao.getAttribute("data-excluir");
    const nome = botao.getAttribute("data-nome");
    textoModal.textContent = `Tem certeza de que deseja excluir o usuário "${nome}"? Esta ação não pode ser desfeita.`;
    modalExclusao.hidden = false;
  });

  botaoCancelar.addEventListener("click", () => {
    idParaExcluir = null;
    modalExclusao.hidden = true;
  });

  botaoConfirmar.addEventListener("click", async () => {
    if (idParaExcluir === null) return;
    await LabControlApi.excluirUsuario(idParaExcluir);
    idParaExcluir = null;
    modalExclusao.hidden = true;
    await carregar();
  });

  await carregar();
});
