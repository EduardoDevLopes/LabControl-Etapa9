/**
 * LabControl — Formulário de equipamento (cadastro e edição)
 *
 * A prévia dinâmica do nível de estoque agora consulta o back-end
 * (GET /api/equipamentos/nivel-estoque?quantidade=N) em vez de
 * recalcular a regra em JavaScript como na Etapa 8 — a lógica de
 * negócio passa a existir em um único lugar (EquipamentoService).
 */

document.addEventListener("DOMContentLoaded", async () => {
  const sessao = await LabControlLayout.iniciarPagina("equipamentos");
  if (!sessao) return;

  const idEdicao = extrairIdDaUrl();

  const form = document.getElementById("form-equipamento");
  const campoId = document.getElementById("id");
  const campoNome = document.getElementById("nome");
  const campoDescricao = document.getElementById("descricao");
  const campoQuantidade = document.getElementById("quantidade");
  const campoStatus = document.getElementById("status");
  const tituloPagina = document.getElementById("titulo-pagina");
  const botaoSalvar = form.querySelector("button[type=submit]");

  const previaNivel = document.getElementById("previa-nivel");
  const etiquetaPreviaNivel = document.getElementById("etiqueta-previa-nivel");

  const erros = {
    nome: document.getElementById("erro-nome"),
    descricao: document.getElementById("erro-descricao"),
    quantidade: document.getElementById("erro-quantidade"),
    status: document.getElementById("erro-status"),
  };

  const camposComErro = {
    nome: campoNome,
    descricao: campoDescricao,
    quantidade: campoQuantidade,
    status: campoStatus,
  };

  let modoEdicao = false;
  let temporizadorPrevia = null;

  function extrairIdDaUrl() {
    // Rota: /equipamentos/{id}/editar
    const partes = window.location.pathname.split("/").filter(Boolean);
    const indice = partes.indexOf("equipamentos");
    if (indice !== -1 && partes[indice + 2] === "editar") {
      return partes[indice + 1];
    }
    return null;
  }

  if (idEdicao) {
    try {
      const equipamento = await LabControlApi.buscarEquipamentoPorId(idEdicao);
      modoEdicao = true;
      tituloPagina.textContent = "Editar equipamento";
      campoId.value = equipamento.id;
      campoNome.value = equipamento.nome;
      campoDescricao.value = equipamento.descricao || "";
      campoQuantidade.value = equipamento.quantidade;
      campoStatus.value = equipamento.status;
    } catch (erro) {
      window.location.href = "/equipamentos";
      return;
    }
  }

  async function atualizarPreviaNivel() {
    const valor = campoQuantidade.value;
    if (valor === "" || Number(valor) < 0 || Number.isNaN(Number(valor))) {
      previaNivel.hidden = true;
      return;
    }

    try {
      const nivel = await LabControlApi.calcularNivelEstoque(Number(valor));
      const rotulo = LabControlApi.NIVEL_ROTULO[nivel];

      etiquetaPreviaNivel.textContent = rotulo;
      etiquetaPreviaNivel.className = "etiqueta etiqueta-" + nivel.toLowerCase();
      previaNivel.hidden = false;
    } catch (erro) {
      previaNivel.hidden = true;
    }
  }

  campoQuantidade.addEventListener("input", () => {
    clearTimeout(temporizadorPrevia);
    temporizadorPrevia = setTimeout(atualizarPreviaNivel, 200);
  });
  atualizarPreviaNivel();

  function limparErros() {
    Object.values(erros).forEach((elemento) => (elemento.textContent = ""));
    Object.values(camposComErro).forEach((campo) => campo.classList.remove("campo-invalido"));
  }

  function marcarErro(chave, mensagem) {
    erros[chave].textContent = mensagem;
    camposComErro[chave].classList.add("campo-invalido");
  }

  function validarNoCliente() {
    let valido = true;

    if (!campoNome.value.trim()) {
      marcarErro("nome", "Informe o nome do equipamento.");
      valido = false;
    }

    const quantidade = campoQuantidade.value;
    if (quantidade === "") {
      marcarErro("quantidade", "Informe a quantidade em estoque.");
      valido = false;
    } else if (Number(quantidade) < 0 || !Number.isInteger(Number(quantidade))) {
      marcarErro("quantidade", "A quantidade deve ser um número inteiro igual ou maior que zero.");
      valido = false;
    }

    if (!campoStatus.value) {
      marcarErro("status", "Selecione o status do equipamento.");
      valido = false;
    }

    return valido;
  }

  form.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    limparErros();

    if (!validarNoCliente()) return;

    const dadosEquipamento = {
      nome: campoNome.value.trim(),
      descricao: campoDescricao.value.trim(),
      quantidade: Number(campoQuantidade.value),
      status: campoStatus.value,
    };

    if (modoEdicao) {
      dadosEquipamento.id = Number(campoId.value);
    }

    botaoSalvar.disabled = true;
    try {
      await LabControlApi.salvarEquipamento(dadosEquipamento);
      window.location.href = "/equipamentos";
    } catch (erro) {
      marcarErro("nome", erro.message || "Não foi possível salvar. Tente novamente.");
    } finally {
      botaoSalvar.disabled = false;
    }
  });
});
