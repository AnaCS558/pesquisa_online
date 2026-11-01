const sessao = Auth.exigirSessao();
if (sessao) {
  UI.montarNavbar("pesquisas");
  document.getElementById("page-title").textContent = sessao.perfil === "Admin" ? "Minhas pesquisas" : "Pesquisas disponíveis";
  carregar();
}

let todasPesquisas = [];
let filtroAtual = "todas";

function pesquisaEncerrada(p) {
  if (!p.ativa) return true;
  if (p.dataEncerramento && new Date(p.dataEncerramento).getTime() < Date.now()) return true;
  return false;
}

async function carregar() {
  const area = document.getElementById("content-area");
  try {
    todasPesquisas = await api.pesquisas.listar();
    todasPesquisas.sort((a, b) => new Date(b.dataCriacao) - new Date(a.dataCriacao));
    renderizar();
  } catch (err) {
    area.innerHTML = `
      <div class="empty-state">
        <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
        <h3>Não foi possível carregar as pesquisas</h3>
        <p>${escapeSafe(err.message)}</p>
        <button class="btn btn--outline" onclick="carregar()">Tentar novamente</button>
      </div>`;
  }
}

function escapeSafe(s) {
  const d = document.createElement("div");
  d.textContent = s ?? "";
  return d.innerHTML;
}

document.getElementById("filter-tabs").addEventListener("click", (e) => {
  const btn = e.target.closest("button[data-filter]");
  if (!btn) return;
  filtroAtual = btn.dataset.filter;
  document.querySelectorAll("#filter-tabs button").forEach((b) => b.classList.toggle("is-active", b === btn));
  renderizar();
});

function renderizar() {
  const area = document.getElementById("content-area");
  const isAdmin = sessao.perfil === "Admin";

  let lista = todasPesquisas;
  if (!isAdmin) lista = lista.filter((p) => !pesquisaEncerrada(p));
  if (filtroAtual === "ativas") lista = lista.filter((p) => !pesquisaEncerrada(p));
  if (filtroAtual === "encerradas") lista = lista.filter((p) => pesquisaEncerrada(p));

  const cards = lista.map((p) => cardPesquisa(p, isAdmin)).join("");
  const novoCard = isAdmin
    ? `
    <button class="survey-new-card" id="new-survey-card" type="button">
      <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
      <span>Nova pesquisa</span>
    </button>`
    : "";

  if (!lista.length && !isAdmin) {
    area.innerHTML = `
      <div class="empty-state">
        <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
        <h3>Nenhuma pesquisa disponível no momento</h3>
        <p>Volte mais tarde para ver novas pesquisas ativas.</p>
      </div>`;
    return;
  }

  area.innerHTML = `<div class="surveys-grid">${novoCard}${cards}</div>`;

  document.getElementById("new-survey-card")?.addEventListener("click", () => {
    window.location.href = "editor.html";
  });

  document.querySelectorAll(".survey-card").forEach((el) => {
    el.addEventListener("click", (e) => {
      if (e.target.closest(".kebab-menu")) return;
      abrirPesquisa(el.dataset.id);
    });
  });

  document.querySelectorAll(".kebab-menu__trigger").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.stopPropagation();
      const panel = btn.nextElementSibling;
      document.querySelectorAll(".kebab-menu__panel.is-open").forEach((p) => {
        if (p !== panel) p.classList.remove("is-open");
      });
      panel.classList.toggle("is-open");
    });
  });
  document.addEventListener("click", () => {
    document.querySelectorAll(".kebab-menu__panel.is-open").forEach((p) => p.classList.remove("is-open"));
  });

  document.querySelectorAll("[data-action]").forEach((btn) => {
    btn.addEventListener("click", async (e) => {
      e.stopPropagation();
      const { action, id } = btn.dataset;
      if (action === "editar") window.location.href = `editor.html?id=${id}`;
      if (action === "respostas") window.location.href = `resultados.html?id=${id}`;
      if (action === "responder") window.location.href = `responder.html?id=${id}`;
      if (action === "encerrar") await encerrarPesquisa(id);
      if (action === "excluir") await excluirPesquisa(id);
    });
  });
}

function abrirPesquisa(id) {
  const p = todasPesquisas.find((x) => String(x.id) === String(id));
  if (sessao.perfil === "Admin") {
    window.location.href = `editor.html?id=${id}`;
  } else if (!pesquisaEncerrada(p)) {
    window.location.href = `responder.html?id=${id}`;
  }
}

function cardPesquisa(p, isAdmin) {
  const encerrada = pesquisaEncerrada(p);
  const descricao = p.descricao ? escapeSafe(p.descricao) : "Sem descrição.";
  const qtdPerguntas = p.perguntas?.length ?? 0;

  const menuAdmin = `
    <div class="kebab-menu">
      <button class="btn btn--ghost btn--icon kebab-menu__trigger" type="button" aria-label="Mais ações">
        <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="5" r="1.5"/><circle cx="12" cy="12" r="1.5"/><circle cx="12" cy="19" r="1.5"/></svg>
      </button>
      <div class="kebab-menu__panel">
        <button type="button" data-action="editar" data-id="${p.id}">
          <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
          Editar
        </button>
        <button type="button" data-action="respostas" data-id="${p.id}">
          <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><path d="M3 3v18h18"/><path d="M18 17V9"/><path d="M13 17V5"/><path d="M8 17v-3"/></svg>
          Ver respostas
        </button>
        ${
          !encerrada
            ? `<button type="button" data-action="encerrar" data-id="${p.id}">
          <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
          Encerrar
        </button>`
            : ""
        }
        <button type="button" class="is-danger" data-action="excluir" data-id="${p.id}">
          <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          Excluir
        </button>
      </div>
    </div>`;

  return `
    <article class="card survey-card" data-id="${p.id}">
      <div class="survey-card__accent"></div>
      <div class="survey-card__top">
        <span class="badge ${encerrada ? "badge--closed" : "badge--active"}">${encerrada ? "Encerrada" : "Ativa"}</span>
        ${isAdmin ? menuAdmin : ""}
      </div>
      <h3>${escapeSafe(p.titulo)}</h3>
      <p>${descricao}</p>
      <div class="survey-card__meta">
        <span>${qtdPerguntas} ${qtdPerguntas === 1 ? "pergunta" : "perguntas"}</span>
        <span>·</span>
        <span>Criada em ${UI.formatarData(p.dataCriacao)}</span>
      </div>
    </article>`;
}

async function encerrarPesquisa(id) {
  const ok = await UI.confirmar({
    titulo: "Encerrar pesquisa",
    mensagem: "Participantes não poderão mais enviar respostas para esta pesquisa. Você pode continuar vendo os resultados.",
    textoConfirmar: "Encerrar",
  });
  if (!ok) return;
  const p = todasPesquisas.find((x) => String(x.id) === String(id));
  try {
    await api.pesquisas.atualizar(id, {
      titulo: p.titulo,
      descricao: p.descricao,
      ativa: false,
      dataEncerramento: new Date().toISOString(),
    });
    UI.toast("Pesquisa encerrada.");
    carregar();
  } catch (err) {
    UI.toast(err.message || "Não foi possível encerrar a pesquisa.", "error");
  }
}

async function excluirPesquisa(id) {
  const ok = await UI.confirmar({
    titulo: "Excluir pesquisa",
    mensagem: "Esta ação remove a pesquisa, suas perguntas, opções e respostas. Não é possível desfazer.",
    textoConfirmar: "Excluir",
    perigoso: true,
  });
  if (!ok) return;
  try {
    await api.pesquisas.excluir(id);
    UI.toast("Pesquisa excluída.");
    carregar();
  } catch (err) {
    UI.toast(err.message || "Não foi possível excluir a pesquisa.", "error");
  }
}
