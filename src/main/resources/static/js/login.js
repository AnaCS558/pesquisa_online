/**
 * login.js
 *
 * Ver aviso em auth.js: como a API não tem endpoint de login nem devolve a
 * senha, aqui apenas conferimos se existe um usuário com o e-mail informado
 * (GET /usuarios/email/{email}). A senha é exigida no formulário por
 * consistência de UX, mas não há como validá-la contra o backend atual.
 */

if (Auth.estaLogado()) {
  window.location.replace("pesquisas.html");
}

const form = document.getElementById("login-form");
const alertBox = document.getElementById("login-alert");
const submitBtn = document.getElementById("login-submit");

function mostrarErro(msg) {
  alertBox.textContent = msg;
  alertBox.classList.add("is-visible");
}

function limparErro() {
  alertBox.classList.remove("is-visible");
  alertBox.textContent = "";
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  limparErro();

  const email = document.getElementById("email").value.trim();
  const senha = document.getElementById("senha").value;

  if (!email || !senha) {
    mostrarErro("Preencha e-mail e senha para continuar.");
    return;
  }

  submitBtn.disabled = true;
  submitBtn.textContent = "Entrando...";

  try {
    const usuario = await api.usuarios.buscarPorEmail(email);
    Auth.salvarSessao(usuario);
    window.location.href = "pesquisas.html";
  } catch (err) {
    if (err.status === 404) {
      mostrarErro("Não encontramos uma conta com esse e-mail. Verifique ou cadastre-se.");
    } else {
      mostrarErro(err.message || "Não foi possível entrar agora.");
    }
    submitBtn.disabled = false;
    submitBtn.textContent = "Entrar";
  }
});