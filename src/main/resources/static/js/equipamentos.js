/**
 * LabControl — Listagem de equipamentos
 * Busca por nome (agora delegada ao back-end via ?busca=), nível de
 * estoque já calculado no DTO, e exclusão com confirmação.
 */

document.addEventListener("DOMContentLoaded", async () => {
  const sessao = await LabControlLayout.iniciarPagina("equipamentos");
  if (!sessao) return;

  const corpoTabela = document.getElementById("corpo-tabela-equipamentos");
  const mensagemSemResultados = document.getElementById("mensagem-sem-resultados");
  const campoBusca = document.getElementById("campo-busca");
  const modalExclusao = document.getElementById("modal-exclusao");
  const textoModal = document.getElementById("modal-exclusao-texto");
  const botaoCancelar = document.getElementById("botao-cancelar-exclusao");
  const botaoConfirmar = document.getElementById("botao-confirmar-exclusao");

  let idParaExcluir = null;
  let temporizadorBusca = null;

  function renderizar(equipamentos) {
    if (equipamentos.length === 0) {
      corpoTabela.innerHTML = "";
      mensagemSemResultados.hidden = false;
      return;
    }

    mensagemSemResultados.hidden = true;
    corpoTabela.innerHTML = equipamentos
      .map((equipamento) => {
        const classeEtiqueta = "etiqueta-" + equipamento.nivelEstoque.toLowerCase();
        const rotuloNivel = LabControlApi.NIVEL_ROTULO[equipamento.nivelEstoque];
        const rotuloStatus = LabControlApi.STATUS_ROTULO[equipamento.status] || equipamento.status;

        return `
          <tr>
            <td class="celula-principal">${equipamento.nome}</td>
            <td>${equipamento.quantidade}</td>
            <td><span class="etiqueta ${classeEtiqueta}">${rotuloNivel}</span></td>
            <td>${rotuloStatus}</td>
            <td class="coluna-acoes">
              <a class="btn btn-secundario" href="/equipamentos/${equipamento.id}/editar">Editar</a>
              <button type="button" class="btn btn-perigo" data-excluir="${equipamento.id}" data-nome="${equipamento.nome}">Excluir</button>
            </td>
          </tr>
        `;
      })
      .join("");
  }

  async function carregar(termoBusca) {
    const equipamentos = await LabControlApi.listarEquipamentos(termoBusca);
    renderizar(equipamentos);
  }

  campoBusca.addEventListener("input", (evento) => {
    clearTimeout(temporizadorBusca);
    const termo = evento.target.value;
    // Pequeno debounce para não disparar uma requisição a cada tecla.
    temporizadorBusca = setTimeout(() => carregar(termo), 250);
  });

  corpoTabela.addEventListener("click", (evento) => {
    const botao = evento.target.closest("[data-excluir]");
    if (!botao) return;

    idParaExcluir = botao.getAttribute("data-excluir");
    const nome = botao.getAttribute("data-nome");
    textoModal.textContent = `Tem certeza de que deseja excluir o equipamento "${nome}"? Esta ação não pode ser desfeita.`;
    modalExclusao.hidden = false;
  });

  botaoCancelar.addEventListener("click", () => {
    idParaExcluir = null;
    modalExclusao.hidden = true;
  });

  botaoConfirmar.addEventListener("click", async () => {
    if (idParaExcluir === null) return;
    await LabControlApi.excluirEquipamento(idParaExcluir);
    idParaExcluir = null;
    modalExclusao.hidden = true;
    await carregar(campoBusca.value);
  });

  await carregar("");
});
