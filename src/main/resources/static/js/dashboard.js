/**
 * LabControl — Dashboard
 * Busca equipamentos e usuários via API REST (o nível de estoque já
 * vem calculado do back-end em cada equipamento — ver EquipamentoDTO)
 * e monta os indicadores e a tabela de alerta de estoque.
 */

document.addEventListener("DOMContentLoaded", async () => {
  const sessao = await LabControlLayout.iniciarPagina("dashboard");
  if (!sessao) return;

  const [equipamentos, usuarios] = await Promise.all([
    LabControlApi.listarEquipamentos(),
    LabControlApi.listarUsuarios(),
  ]);

  const totalEquipamentos = equipamentos.length;
  const emAlerta = equipamentos.filter(
    (equipamento) =>
      equipamento.nivelEstoque === "CRITICO" ||
      equipamento.nivelEstoque === "ESGOTADO" ||
      equipamento.nivelEstoque === "BAIXO"
  );

  document.getElementById("indicadores").innerHTML = `
    <div class="indicador">
      <p class="indicador__rotulo">Total de equipamentos</p>
      <p class="indicador__valor">${totalEquipamentos}</p>
    </div>
    <div class="indicador indicador--alerta">
      <p class="indicador__rotulo">Estoque baixo ou crítico</p>
      <p class="indicador__valor">${emAlerta.length}</p>
    </div>
    <div class="indicador">
      <p class="indicador__rotulo">Usuários cadastrados</p>
      <p class="indicador__valor">${usuarios.length}</p>
    </div>
  `;

  const corpoTabela = document.getElementById("corpo-tabela-alerta");
  const mensagemSemAlertas = document.getElementById("mensagem-sem-alertas");
  const tabela = document.getElementById("tabela-alerta-estoque");

  if (emAlerta.length === 0) {
    tabela.hidden = true;
    mensagemSemAlertas.hidden = false;
    return;
  }

  corpoTabela.innerHTML = emAlerta
    .sort((a, b) => a.quantidade - b.quantidade)
    .map((equipamento) => {
      const classeEtiqueta = "etiqueta-" + equipamento.nivelEstoque.toLowerCase();
      const rotuloNivel = LabControlApi.NIVEL_ROTULO[equipamento.nivelEstoque];
      return `
        <tr>
          <td class="celula-principal">${equipamento.nome}</td>
          <td>${equipamento.quantidade}</td>
          <td><span class="etiqueta ${classeEtiqueta}">${rotuloNivel}</span></td>
        </tr>
      `;
    })
    .join("");
});
