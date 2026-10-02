/**
 * LabControl — Camada de comunicação com a API REST
 *
 * Substitui mock-data.js da Etapa 8. Onde antes as funções liam e
 * escreviam em sessionStorage, agora fazem fetch() para os
 * controllers REST do back-end (AuthController, UsuarioApiController,
 * EquipamentoApiController). A assinatura das funções que já
 * existiam (listarUsuarios, salvarEquipamento, autenticar etc.) foi
 * mantida sempre que possível, para que dashboard.js, usuarios.js e
 * os demais scripts de página precisassem de poucas mudanças.
 *
 * Toda função aqui é assíncrona (retorna Promise), já que agora
 * envolve uma chamada de rede real — isso é a principal mudança que
 * se propaga para quem consome este arquivo.
 */

const LabControlApi = (() => {
  const BASE = "/api";

  async function requisitar(caminho, opcoes = {}) {
    const resposta = await fetch(BASE + caminho, {
      headers: { "Content-Type": "application/json" },
      credentials: "same-origin",
      ...opcoes,
    });

    if (resposta.status === 204) {
      return null;
    }

    const corpo = await resposta.json().catch(() => null);

    if (!resposta.ok) {
      const mensagem = corpo && corpo.mensagens && corpo.mensagens[0]
        ? corpo.mensagens[0]
        : "Ocorreu um erro inesperado.";
      const erro = new Error(mensagem);
      erro.status = resposta.status;
      erro.corpo = corpo;
      throw erro;
    }

    return corpo;
  }

  // ---- Autenticação ----

  async function autenticar(login, senha) {
    try {
      return await requisitar("/auth/login", {
        method: "POST",
        body: JSON.stringify({ login, senha }),
      });
    } catch (erro) {
      if (erro.status === 401) return null;
      throw erro;
    }
  }

  async function sessaoAtual() {
    try {
      return await requisitar("/auth/sessao");
    } catch (erro) {
      if (erro.status === 401) return null;
      throw erro;
    }
  }

  async function encerrarSessao() {
    await requisitar("/auth/logout", { method: "POST" });
  }

  /**
   * Garante que existe uma sessão ativa; caso contrário, redireciona
   * para a tela de login. Assim como na Etapa 8, chamado no topo de
   * todas as páginas internas — mas agora a checagem é validada
   * contra o servidor (via /api/auth/sessao), não contra
   * sessionStorage local.
   */
  async function exigirSessao() {
    const sessao = await sessaoAtual();
    if (!sessao) {
      window.location.href = "/login";
      return null;
    }
    return sessao;
  }

  // ---- Usuários ----

  function listarUsuarios() {
    return requisitar("/usuarios");
  }

  function buscarUsuarioPorId(id) {
    return requisitar("/usuarios/" + id);
  }

  function salvarUsuario(dadosUsuario) {
    if (dadosUsuario.id) {
      return requisitar("/usuarios/" + dadosUsuario.id, {
        method: "PUT",
        body: JSON.stringify(dadosUsuario),
      });
    }
    return requisitar("/usuarios", {
      method: "POST",
      body: JSON.stringify(dadosUsuario),
    });
  }

  function excluirUsuario(id) {
    return requisitar("/usuarios/" + id, { method: "DELETE" });
  }

  // ---- Equipamentos ----

  function listarEquipamentos(busca) {
    const query = busca ? "?busca=" + encodeURIComponent(busca) : "";
    return requisitar("/equipamentos" + query);
  }

  function buscarEquipamentoPorId(id) {
    return requisitar("/equipamentos/" + id);
  }

  function salvarEquipamento(dadosEquipamento) {
    if (dadosEquipamento.id) {
      return requisitar("/equipamentos/" + dadosEquipamento.id, {
        method: "PUT",
        body: JSON.stringify(dadosEquipamento),
      });
    }
    return requisitar("/equipamentos", {
      method: "POST",
      body: JSON.stringify(dadosEquipamento),
    });
  }

  function excluirEquipamento(id) {
    return requisitar("/equipamentos/" + id, { method: "DELETE" });
  }

  /**
   * Consulta o nível de estoque calculado pelo back-end para uma
   * quantidade — usado pela prévia dinâmica no formulário de
   * equipamento. Substitui o cálculo local que existia em
   * mock-data.js (Etapa 8); a regra agora vive só no servidor
   * (EquipamentoService.calcularNivelEstoque).
   */
  function calcularNivelEstoque(quantidade) {
    return requisitar("/equipamentos/nivel-estoque?quantidade=" + quantidade);
  }

  const NIVEL_ROTULO = {
    ESGOTADO: "Esgotado",
    CRITICO: "Crítico",
    BAIXO: "Baixo",
    NORMAL: "Normal",
  };

  const STATUS_ROTULO = {
    DISPONIVEL: "Disponível",
    EM_USO: "Em uso",
    MANUTENCAO: "Em manutenção",
  };

  const PERFIL_ROTULO = {
    ADMINISTRADOR: "Administrador",
    TECNICO_LABORATORIO: "Técnico de laboratório",
    OPERADOR: "Operador",
  };

  return {
    autenticar,
    sessaoAtual,
    encerrarSessao,
    exigirSessao,
    listarUsuarios,
    buscarUsuarioPorId,
    salvarUsuario,
    excluirUsuario,
    listarEquipamentos,
    buscarEquipamentoPorId,
    salvarEquipamento,
    excluirEquipamento,
    calcularNivelEstoque,
    NIVEL_ROTULO,
    STATUS_ROTULO,
    PERFIL_ROTULO,
  };
})();
