/**
 * LabControl — Tela de login
 * Validação de campos obrigatórios e autenticação via
 * LabControlApi.autenticar() (POST /api/auth/login), que cria uma
 * sessão HTTP real no servidor — substituindo a autenticação
 * simulada em sessionStorage da Etapa 8.
 */

document.addEventListener("DOMContentLoaded", async () => {
  // Se já existe sessão ativa, pula direto para o dashboard.
  const sessaoExistente = await LabControlApi.sessaoAtual();
  if (sessaoExistente) {
    window.location.href = "/dashboard";
    return;
  }

  const form = document.getElementById("form-login");
  const campoLogin = document.getElementById("login");
  const campoSenha = document.getElementById("senha");
  const erroLogin = document.getElementById("erro-login");
  const erroSenha = document.getElementById("erro-senha");
  const alerta = document.getElementById("alerta-login");
  const botaoEntrar = form.querySelector("button[type=submit]");

  function limparErros() {
    campoLogin.classList.remove("campo-invalido");
    campoSenha.classList.remove("campo-invalido");
    erroLogin.textContent = "";
    erroSenha.textContent = "";
    alerta.hidden = true;
    alerta.textContent = "";
  }

  function validar() {
    let valido = true;

    if (!campoLogin.value.trim()) {
      campoLogin.classList.add("campo-invalido");
      erroLogin.textContent = "Informe o login.";
      valido = false;
    }

    if (!campoSenha.value) {
      campoSenha.classList.add("campo-invalido");
      erroSenha.textContent = "Informe a senha.";
      valido = false;
    }

    return valido;
  }

  form.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    limparErros();

    if (!validar()) return;

    botaoEntrar.disabled = true;
    try {
      const sessao = await LabControlApi.autenticar(campoLogin.value.trim(), campoSenha.value);

      if (!sessao) {
        alerta.hidden = false;
        alerta.textContent = "Login ou senha inválidos.";
        return;
      }

      window.location.href = "/dashboard";
    } catch (erro) {
      alerta.hidden = false;
      alerta.textContent = erro.message || "Não foi possível entrar. Tente novamente.";
    } finally {
      botaoEntrar.disabled = false;
    }
  });
});
