if (Auth.estaLogado()) {
  window.location.replace("pesquisas.html");
}

const form = document.getElementById("cadastro-form");
const alertBox = document.getElementById("cadastro-alert");
const submitBtn = document.getElementById("cadastro-submit");

function mostrarErro(msg) {
  alertBox.textContent = msg;
  alertBox.classList.add("is-visible");
}
function limparErro() {
  alertBox.classList.remove("is-visible");
  alertBox.textContent = "";
}
function limparCamposErro() {
  ["nome", "email", "senha"].forEach((id) => {
    document.getElementById(id).classList.remove("is-invalid");
    document.getElementById(`${id}-error`).textContent = "";
  });
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  limparErro();
  limparCamposErro();

  const dto = {
    nome: document.getElementById("nome").value.trim(),
    email: document.getElementById("email").value.trim(),
    senha: document.getElementById("senha").value,
    perfil: document.getElementById("perfil").value,
  };

  if (!dto.nome || !dto.email || !dto.senha) {
    mostrarErro("Preencha todos os campos para continuar.");
    return;
  }
  if (dto.senha.length < 6) {
    document.getElementById("senha").classList.add("is-invalid");
    document.getElementById("senha-error").textContent = "A senha deve ter pelo menos 6 caracteres.";
    return;
  }

  submitBtn.disabled = true;
  submitBtn.textContent = "Criando conta...";

  try {
    const usuario = await api.usuarios.salvar(dto);
    Auth.salvarSessao(usuario);
    window.location.href = "pesquisas.html";
  } catch (err) {
    if (err.status === 409 || /já (está|existe|cadastrad)/i.test(err.message || "")) {
      mostrarErro("Já existe uma conta com esse e-mail. Tente entrar.");
    } else if (err.status === 400) {
      mostrarErro(err.message || "Verifique os dados informados.");
    } else {
      mostrarErro(err.message || "Não foi possível criar sua conta agora.");
    }
    submitBtn.disabled = false;
    submitBtn.textContent = "Criar conta";
  }
});