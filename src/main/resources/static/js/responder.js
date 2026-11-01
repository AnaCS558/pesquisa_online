const sessao = Auth.exigirSessao();
const params = new URLSearchParams(window.location.search);
const pesquisaId = params.get("id");

let pesquisa = null;
const respostasPorPergunta = {}; // perguntaId -> { texto } | { opcaoId } | { opcaoIds: Set }

if (sessao) {
  UI.montarNavbar("pesquisas");
  if (!pesquisaId) {
    mostrarErroCarregamento("Pesquisa não informada.");
  } else {
    carregar();
  }
}

async function carregar() {
  try {
    pesquisa = await api.pesquisas.buscarPorId(pesquisaId);
  } catch (err) {
    mostrarErroCarregamento(err.status === 404 ? "Esta pesquisa não existe mais." : err.message);
    return;
  }

  const encerrada = !pesquisa.ativa || (pesquisa.dataEncerramento && new Date(pesquisa.dataEncerramento).getTime() < Date.now());
  if (encerrada) {
    mostrarEncerrada();
    return;
  }

  pesquisa.perguntas = (pesquisa.perguntas || []).slice().sort((a, b) => a.ordem - b.ordem);
  render();
}

function mostrarErroCarregamento(msg) {
  document.getElementById("content-area").innerHTML = `
    <div class="empty-state">
      <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
      <h3>Não foi possível abrir esta pesquisa</h3>
      <p>${esc(msg)}</p>
    </div>`;
}

function mostrarEncerrada() {
  document.getElementById("content-area").innerHTML = `
    <div class="respond-head">
      <h1>${esc(pesquisa.titulo)}</h1>
      <p>Esta pesquisa foi encerrada e não está recebendo novas respostas.</p>
    </div>
    <a class="btn btn--outline" href="pesquisas.html">Voltar para pesquisas</a>`;
}

function render() {
  const perguntasHtml = pesquisa.perguntas.map(renderPergunta).join("");

  document.getElementById("content-area").innerHTML = `
    <section class="respond-head">
      <h1>${esc(pesquisa.titulo)}</h1>
      ${pesquisa.descricao ? `<p>${esc(pesquisa.descricao)}</p>` : ""}
      <p class="respond-head__required">* Pergunta obrigatória</p>
    </section>

    ${perguntasHtml || `<div class="empty-state"><p>Esta pesquisa ainda não tem perguntas.</p></div>`}

    ${
      pesquisa.perguntas.length
        ? `
      <div class="respond-submit">
        <span class="field__hint">Suas respostas são enviadas como ${esc(sessao.nome)}.</span>
        <button class="btn btn--accent btn--lg" id="btn-enviar">Enviar respostas</button>
      </div>`
        : ""
    }
  `;

  document.getElementById("btn-enviar")?.addEventListener("click", enviar);

  pesquisa.perguntas.forEach((p) => {
    const card = document.querySelector(`[data-pergunta-id="${p.id}"]`);
    if (p.tipoPergunta === "Aberta") {
      card.querySelector("textarea").addEventListener("input", (e) => {
        respostasPorPergunta[p.id] = { texto: e.target.value };
        card.classList.remove("is-invalid");
      });
    } else if (p.tipoPergunta === "Unica_Escolha") {
      card.querySelectorAll('input[type="radio"]').forEach((input) => {
        input.addEventListener("change", () => {
          respostasPorPergunta[p.id] = { opcaoId: Number(input.value) };
          card.classList.remove("is-invalid");
          card.querySelectorAll(".radio-row").forEach((r) => r.classList.toggle("is-checked", r.querySelector("input").checked));
        });
      });
    } else {
      card.querySelectorAll('input[type="checkbox"]').forEach((input) => {
        input.addEventListener("change", () => {
          const atual = respostasPorPergunta[p.id]?.opcaoIds || new Set();
          if (input.checked) atual.add(Number(input.value));
          else atual.delete(Number(input.value));
          respostasPorPergunta[p.id] = { opcaoIds: atual };
          card.classList.remove("is-invalid");
          input.closest(".checkbox-row").classList.toggle("is-checked", input.checked);
        });
      });
    }
  });
}

function renderPergunta(p) {
  let campo = "";
  if (p.tipoPergunta === "Aberta") {
    campo = `<textarea class="textarea" placeholder="Sua resposta" maxlength="1000"></textarea>`;
  } else if (p.tipoPergunta === "Unica_Escolha") {
    campo = `<div class="respond-card__options">
      ${p.opcoes
        .map(
          (o) => `
        <label class="radio-row">
          <input type="radio" name="pergunta-${p.id}" value="${o.id}">
          <span>${esc(o.descricao)}</span>
        </label>`
        )
        .join("")}
    </div>`;
  } else {
    campo = `<div class="respond-card__options">
      ${p.opcoes
        .map(
          (o) => `
        <label class="checkbox-row">
          <input type="checkbox" value="${o.id}">
          <span>${esc(o.descricao)}</span>
        </label>`
        )
        .join("")}
    </div>`;
  }

  return `
    <article class="respond-card" data-pergunta-id="${p.id}">
      <p class="respond-card__q">${esc(p.enunciado)}${p.obrigatoria ? '<span class="req">*</span>' : ""}</p>
      ${campo}
      <p class="respond-card__err">Esta pergunta é obrigatória.</p>
    </article>`;
}

function esc(s) {
  const d = document.createElement("div");
  d.textContent = s ?? "";
  return d.innerHTML;
}

function respostaPreenchida(p) {
  const r = respostasPorPergunta[p.id];
  if (!r) return false;
  if (p.tipoPergunta === "Aberta") return !!r.texto && r.texto.trim().length > 0;
  if (p.tipoPergunta === "Unica_Escolha") return !!r.opcaoId;
  return !!r.opcaoIds && r.opcaoIds.size > 0;
}

async function enviar() {
  let valido = true;
  pesquisa.perguntas.forEach((p) => {
    if (p.obrigatoria && !respostaPreenchida(p)) {
      valido = false;
      document.querySelector(`[data-pergunta-id="${p.id}"]`).classList.add("is-invalid");
    }
  });
  if (!valido) {
    UI.toast("Responda as perguntas obrigatórias antes de enviar.", "error");
    document.querySelector(".respond-card.is-invalid")?.scrollIntoView({ behavior: "smooth", block: "center" });
    return;
  }

  const btn = document.getElementById("btn-enviar");
  btn.disabled = true;
  btn.textContent = "Enviando...";

  const chamadas = [];
  pesquisa.perguntas.forEach((p) => {
    const r = respostasPorPergunta[p.id];
    if (!r) return;
    if (p.tipoPergunta === "Aberta") {
      if (r.texto && r.texto.trim()) {
        chamadas.push(api.respostas.salvar({ usuarioId: sessao.id, perguntaId: p.id, respostaTexto: r.texto.trim() }));
      }
    } else if (p.tipoPergunta === "Unica_Escolha") {
      if (r.opcaoId) {
        chamadas.push(api.respostas.salvar({ usuarioId: sessao.id, perguntaId: p.id, opcaoId: r.opcaoId }));
      }
    } else if (r.opcaoIds) {
      r.opcaoIds.forEach((opcaoId) => {
        chamadas.push(api.respostas.salvar({ usuarioId: sessao.id, perguntaId: p.id, opcaoId }));
      });
    }
  });

  try {
    await Promise.all(chamadas);
    mostrarConfirmacao();
  } catch (err) {
    UI.toast(err.message || "Não foi possível enviar suas respostas.", "error");
    btn.disabled = false;
    btn.textContent = "Enviar respostas";
  }
}

function mostrarConfirmacao() {
  document.getElementById("content-area").innerHTML = `
    <div class="confirm-screen">
      <div class="confirm-screen__icon">
        <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><polyline points="20 6 9 17 4 12"/></svg>
      </div>
      <h1>Respostas enviadas</h1>
      <p>Obrigado por participar da pesquisa "${esc(pesquisa.titulo)}".</p>
      <a class="btn btn--primary" href="pesquisas.html">Voltar para pesquisas</a>
    </div>`;
}