/**
 * LabControl — Formulário de usuário (cadastro e edição)
 *
 * A mesma página atende cadastro (/usuarios/novo) e edição
 * (/usuarios/{id}/editar): o id é lido da própria URL (via path, não
 * mais query string como na Etapa 8, para casar com as rotas do
 * PaginaController). Validações de formato continuam no cliente para
 * feedback imediato, mas a validação de login duplicado agora é
 * definitivamente aplicada pelo back-end (UsuarioService) — o
 * servidor é a fonte da verdade.
 */

document.addEventListener("DOMContentLoaded", async () => {
  const sessao = await LabControlLayout.iniciarPagina("usuarios");
  if (!sessao) return;

  const idEdicao = extrairIdDaUrl();

  const form = document.getElementById("form-usuario");
  const campoId = document.getElementById("id");
  const campoNome = document.getElementById("nome");
  const campoEmail = document.getElementById("email");
  const campoLogin = document.getElementById("login");
  const campoSenha = document.getElementById("senha");
  const campoPerfil = document.getElementById("perfil");
  const tituloPagina = document.getElementById("titulo-pagina");
  const botaoSalvar = form.querySelector("button[type=submit]");

  const erros = {
    nome: document.getElementById("erro-nome"),
    email: document.getElementById("erro-email"),
    login: document.getElementById("erro-login"),
    senha: document.getElementById("erro-senha"),
    perfil: document.getElementById("erro-perfil"),
  };

  const camposComErro = {
    nome: campoNome,
    email: campoEmail,
    login: campoLogin,
    senha: campoSenha,
    perfil: campoPerfil,
  };

  let modoEdicao = false;

  function extrairIdDaUrl() {
    // Rota: /usuarios/{id}/editar
    const partes = window.location.pathname.split("/").filter(Boolean);
    const indice = partes.indexOf("usuarios");
    if (indice !== -1 && partes[indice + 2] === "editar") {
      return partes[indice + 1];
    }
    return null;
  }

  if (idEdicao) {
    try {
      const usuario = await LabControlApi.buscarUsuarioPorId(idEdicao);
      modoEdicao = true;
      tituloPagina.textContent = "Editar usuário";
      campoId.value = usuario.id;
      campoNome.value = usuario.nome;
      campoEmail.value = usuario.email;
      campoLogin.value = usuario.login;
      campoPerfil.value = usuario.perfil;
      campoSenha.placeholder = "Deixe em branco para manter a senha atual";
    } catch (erro) {
      window.location.href = "/usuarios";
      return;
    }
  }

  function limparErros() {
    Object.values(erros).forEach((elemento) => (elemento.textContent = ""));
    Object.values(camposComErro).forEach((campo) => campo.classList.remove("campo-invalido"));
  }

  function marcarErro(chave, mensagem) {
    erros[chave].textContent = mensagem;
    camposComErro[chave].classList.add("campo-invalido");
  }

  function emailValido(valor) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor);
  }

  function validarNoCliente() {
    let valido = true;

    if (!campoNome.value.trim()) {
      marcarErro("nome", "Informe o nome completo.");
      valido = false;
    }

    if (!campoEmail.value.trim()) {
      marcarErro("email", "Informe o e-mail.");
      valido = false;
    } else if (!emailValido(campoEmail.value.trim())) {
      marcarErro("email", "Informe um e-mail em formato válido.");
      valido = false;
    }

    if (!campoLogin.value.trim()) {
      marcarErro("login", "Informe o login.");
      valido = false;
    }

    if (!modoEdicao && !campoSenha.value) {
      marcarErro("senha", "Informe uma senha para o novo usuário.");
      valido = false;
    } else if (campoSenha.value && campoSenha.value.length < 6) {
      marcarErro("senha", "A senha deve ter ao menos 6 caracteres.");
      valido = false;
    }

    if (!campoPerfil.value) {
      marcarErro("perfil", "Selecione um perfil.");
      valido = false;
    }

    return valido;
  }

  form.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    limparErros();

    if (!validarNoCliente()) return;

    const dadosUsuario = {
      nome: campoNome.value.trim(),
      email: campoEmail.value.trim(),
      login: campoLogin.value.trim(),
      perfil: campoPerfil.value,
    };

    if (modoEdicao) {
      dadosUsuario.id = Number(campoId.value);
      if (campoSenha.value) {
        dadosUsuario.senha = campoSenha.value;
      }
    } else {
      dadosUsuario.senha = campoSenha.value;
    }

    botaoSalvar.disabled = true;
    try {
      await LabControlApi.salvarUsuario(dadosUsuario);
      window.location.href = "/usuarios";
    } catch (erro) {
      // O back-end é quem detecta, por exemplo, login duplicado —
      // aqui a mensagem devolvida pela API é mapeada para o campo
      // mais provável, com um fallback genérico se não for possível.
      if (erro.message && erro.message.toLowerCase().includes("login")) {
        marcarErro("login", erro.message);
      } else {
        marcarErro("nome", erro.message || "Não foi possível salvar. Tente novamente.");
      }
    } finally {
      botaoSalvar.disabled = false;
    }
  });
});
