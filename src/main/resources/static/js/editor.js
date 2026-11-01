/**
 * editor.js — construtor de pesquisas (estilo Google Forms), restrito a
 * usuários com perfil Admin.
 *
 * Como a API separa Pesquisa / Pergunta / Opção em recursos próprios (sem
 * endpoint de salvamento em lote), o fluxo de edição é:
 *   1. Salvar os dados da pesquisa (POST ao criar, PUT ao editar).
 *   2. Com a pesquisa salva, cada pergunta é salva individualmente
 *      (POST /perguntas na primeira vez, PUT depois).
 *   3. Cada opção de uma pergunta de escolha é salva individualmente
 *      (POST /opcoes ao adicionar, PUT ao editar o texto, DELETE ao remover).
 */

const sessao = Auth.exigirAdmin();

const params = new URLSearchParams(window.location.search);
const pesquisaId = params.get("id");

let pesquisa = { titulo: "", descricao: "", ativa: true, dataEncerramento: null };
let perguntas = []; // ver uid() / mapearPerguntaApi() para o formato de cada item

function uid() {
  return "tmp-" + Math.random().toString(36).slice(2, 10);
}

function mapearPerguntaApi(p) {
  return {
    _localId: uid(),
    id: p.id,
    enunciado: p.enunciado,
    tipoPergunta: p.tipoPergunta,
    obrigatoria: !!p.obrigatoria,
    ordem: p.ordem,
    _savedOrdem: p.ordem,
    opcoes: (p.opcoes || []).map((o) => ({ _localId: uid(), id: o.id, descricao: o.descricao })),
  };
}

function novaPerguntaLocal() {
  return {
    _localId: uid(),
    id: null,
    enunciado: "",
    tipoPergunta: "Unica_Escolha",
    obrigatoria: true,
    ordem: perguntas.length + 1,
    _savedOrdem: null,
    opcoes: [],
  };
}

if (sessao) {
  UI.montarNavbar("pesquisas");
  iniciar();
}

async function iniciar() {
  if (pesquisaId) {
    try {
      const dto = await api.pesquisas.buscarPorId(pesquisaId);
      pesquisa = {
        titulo: dto.titulo,
        descricao: dto.descricao || "",
        ativa: !!dto.ativa,
        dataEncerramento: dto.dataEncerramento || null,
      };
      perguntas = (dto.perguntas || []).sort((a, b) => a.ordem - b.ordem).map(mapearPerguntaApi);
    } catch (err) {
      document.getElementById("content-area").innerHTML = `
        <div class="empty-state">
          <h3>Não foi possível carregar esta pesquisa</h3>
          <p>${err.message}</p>
        </div>`;
      return;
    }
  }
  render();
}

function render() {
  const area = document.getElementById("content-area");
  const temPesquisa = !!pesquisaId;

  area.innerHTML = `
    <section class="editor-head">
      <input class="input" id="f-titulo" placeholder="Título da pesquisa" value="${attr(pesquisa.titulo)}" maxlength="150">
      <textarea class="textarea" id="f-descricao" placeholder="Descrição (opcional)" maxlength="500">${esc(pesquisa.descricao)}</textarea>
      <div class="editor-head__row">
        <label class="switch">
          <input type="checkbox" id="f-ativa" ${pesquisa.ativa ? "checked" : ""}>
          <span class="switch__track"></span>
          <span class="switch__label">${pesquisa.ativa ? "Pesquisa ativa" : "Pesquisa inativa"}</span>
        </label>
        <div class="field" style="margin-bottom:0;">
          <label for="f-encerramento">Encerramento automático (opcional)</label>
          <input class="input" type="datetime-local" id="f-encerramento" value="${toLocalInput(pesquisa.dataEncerramento)}">
        </div>
        <button class="btn btn--primary" id="btn-salvar-pesquisa" style="margin-left:auto;">
          ${temPesquisa ? "Salvar alterações" : "Criar pesquisa"}
        </button>
      </div>
    </section>

    ${
      temPesquisa
        ? `
      <div class="toolbar">
        <h2>Perguntas</h2>
        <a class="btn btn--outline btn--sm" href="responder.html?id=${pesquisaId}" target="_blank" rel="noopener">Pré-visualizar</a>
      </div>
      <div id="perguntas-list">${perguntas.map(renderPergunta).join("") || semPerguntas()}</div>
      <button class="add-question-bar" id="btn-add-pergunta" type="button">+ Adicionar pergunta</button>
    `
        : `<p class="field__hint">Salve a pesquisa para começar a adicionar perguntas.</p>`
    }
  `;

  bindEventos();
}

function semPerguntas() {
  return `<div class="empty-state"><p>Nenhuma pergunta ainda. Use o botão abaixo para adicionar a primeira.</p></div>`;
}

function renderPergunta(p, idx) {
  const requerOpcoes = p.tipoPergunta !== "Aberta";
  const statusSalva = !!p.id && !p._dirty;
  const statusCls = p.id ? (p._dirty ? "" : "is-saved") : "is-unsaved";

  return `
    <article class="question-card ${statusCls} ${p.tipoPergunta === "Multipla_Escolha" ? "is-multipla" : ""}" data-local-id="${p._localId}">
      <div class="question-card__head">
        <div class="field" style="margin-bottom:0;">
          <label>Pergunta ${idx + 1}</label>
          <input class="input campo-enunciado" type="text" placeholder="Digite a pergunta" value="${attr(p.enunciado)}" maxlength="500">
        </div>
        <div class="field" style="margin-bottom:0;">
          <label>Tipo de resposta</label>
          <select class="select campo-tipo">
            <option value="Aberta" ${p.tipoPergunta === "Aberta" ? "selected" : ""}>Resposta curta</option>
            <option value="Unica_Escolha" ${p.tipoPergunta === "Unica_Escolha" ? "selected" : ""}>Múltipla escolha</option>
            <option value="Multipla_Escolha" ${p.tipoPergunta === "Multipla_Escolha" ? "selected" : ""}>Caixas de seleção</option>
          </select>
        </div>
      </div>

      ${
        requerOpcoes
          ? `
        <div class="question-card__options">
          ${p.opcoes.map((o) => renderOpcao(o, p)).join("")}
        </div>
        <button class="btn btn--ghost btn--sm add-opcao" type="button">+ Adicionar opção</button>
      `
          : `<p class="field__hint">Perguntas de resposta curta não usam opções — o participante digita a resposta.</p>`
      }

      <div class="question-card__foot">
        <div class="question-card__left-actions">
          <label class="switch">
            <input type="checkbox" class="campo-obrigatoria" ${p.obrigatoria ? "checked" : ""}>
            <span class="switch__track"></span>
            <span class="switch__label">Obrigatória</span>
          </label>
          <button class="btn btn--icon btn--ghost mover-cima" type="button" aria-label="Mover para cima" ${idx === 0 ? "disabled" : ""}>
            <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><line x1="12" y1="19" x2="12" y2="5"/><polyline points="5 12 12 5 19 12"/></svg>
          </button>
          <button class="btn btn--icon btn--ghost mover-baixo" type="button" aria-label="Mover para baixo" ${idx === perguntas.length - 1 ? "disabled" : ""}>
            <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><line x1="12" y1="5" x2="12" y2="19"/><polyline points="19 12 12 19 5 12"/></svg>
          </button>
        </div>
        <div class="question-card__right-actions">
          <span class="question-card__status ${statusSalva ? "is-ok" : ""}">${statusSalva ? "Salva" : "Não salva"}</span>
          <button class="btn btn--danger btn--sm excluir-pergunta" type="button">Excluir</button>
          <button class="btn btn--primary btn--sm salvar-pergunta" type="button">Salvar pergunta</button>
        </div>
      </div>
    </article>`;
}

function renderOpcao(o, pergunta) {
  return `
    <div class="option-row" data-local-id="${o._localId}">
      <span class="option-row__bullet"></span>
      <input class="input campo-opcao-descricao" type="text" placeholder="Texto da opção" value="${attr(o.descricao)}" maxlength="200">
      ${o.id ? `<button class="btn btn--icon btn--ghost salvar-opcao" type="button" title="Salvar opção">${iconCheck()}</button>` : ""}
      <button class="btn btn--icon btn--ghost remover-opcao" type="button" title="Remover opção">${iconTrash()}</button>
    </div>`;
}

function iconCheck() {
  return `<svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><polyline points="20 6 9 17 4 12"/></svg>`;
}
function iconTrash() {
  return `<svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>`;
}

function esc(s) {
  const d = document.createElement("div");
  d.textContent = s ?? "";
  return d.innerHTML;
}
function attr(s) {
  return esc(s).replace(/"/g, "&quot;");
}
function toLocalInput(iso) {
  if (!iso) return "";
  const d = new Date(iso);
  if (isNaN(d.getTime())) return "";
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

/* ========================= Eventos ========================= */

function bindEventos() {
  document.getElementById("btn-salvar-pesquisa").addEventListener("click", salvarPesquisa);
  document.getElementById("f-ativa")?.addEventListener("change", (e) => {
    e.target.closest(".switch").querySelector(".switch__label").textContent = e.target.checked ? "Pesquisa ativa" : "Pesquisa inativa";
  });

  document.getElementById("btn-add-pergunta")?.addEventListener("click", () => {
    perguntas.push(novaPerguntaLocal());
    render();
    document.querySelector(".question-card:last-of-type .campo-enunciado")?.focus();
  });

  document.querySelectorAll(".question-card").forEach((card) => {
    const localId = card.dataset.localId;
    const pergunta = perguntas.find((p) => p._localId === localId);

    card.querySelector(".campo-enunciado").addEventListener("input", (e) => {
      pergunta.enunciado = e.target.value;
      marcarSuja(pergunta, card);
    });
    card.querySelector(".campo-tipo").addEventListener("change", (e) => {
      pergunta.tipoPergunta = e.target.value;
      marcarSuja(pergunta, card);
      render();
    });
    card.querySelector(".campo-obrigatoria").addEventListener("change", (e) => {
      pergunta.obrigatoria = e.target.checked;
      marcarSuja(pergunta, card);
    });
    card.querySelector(".mover-cima")?.addEventListener("click", () => moverPergunta(pergunta, -1));
    card.querySelector(".mover-baixo")?.addEventListener("click", () => moverPergunta(pergunta, 1));
    card.querySelector(".excluir-pergunta").addEventListener("click", () => excluirPergunta(pergunta));
    card.querySelector(".salvar-pergunta").addEventListener("click", (btn) => salvarPergunta(pergunta, btn.currentTarget));
    card.querySelector(".add-opcao")?.addEventListener("click", () => adicionarOpcao(pergunta));

    card.querySelectorAll(".option-row").forEach((row) => {
      const oLocalId = row.dataset.localId;
      const opcao = pergunta.opcoes.find((o) => o._localId === oLocalId);
      row.querySelector(".campo-opcao-descricao").addEventListener("input", (e) => {
        opcao.descricao = e.target.value;
        opcao._dirty = true;
      });
      row.querySelector(".salvar-opcao")?.addEventListener("click", () => salvarOpcaoExistente(pergunta, opcao));
      row.querySelector(".remover-opcao").addEventListener("click", () => removerOpcao(pergunta, opcao));
    });
  });
}

function marcarSuja(pergunta, card) {
  pergunta._dirty = true;
  card.classList.remove("is-saved");
  card.classList.add(pergunta.id ? "" : "is-unsaved");
  const status = card.querySelector(".question-card__status");
  status.textContent = "Não salva";
  status.classList.remove("is-ok");
}

async function salvarPesquisa() {
  const titulo = document.getElementById("f-titulo").value.trim();
  const descricao = document.getElementById("f-descricao").value.trim();
  const ativa = document.getElementById("f-ativa").checked;
  const encerramentoRaw = document.getElementById("f-encerramento").value;

  if (!titulo) {
    UI.toast("Dê um título para a pesquisa antes de salvar.", "error");
    return;
  }

  const dto = {
    titulo,
    descricao: descricao || null,
    ativa,
    dataEncerramento: encerramentoRaw ? new Date(encerramentoRaw).toISOString() : null,
  };

  const btn = document.getElementById("btn-salvar-pesquisa");
  btn.disabled = true;

  try {
    if (pesquisaId) {
      await api.pesquisas.atualizar(pesquisaId, dto);
      UI.toast("Pesquisa atualizada.");
      pesquisa = dto;
      render();
    } else {
      const criada = await api.pesquisas.salvar(dto);
      UI.toast("Pesquisa criada. Agora adicione as perguntas.");
      window.location.href = `editor.html?id=${criada.id}`;
    }
  } catch (err) {
    UI.toast(err.message || "Não foi possível salvar a pesquisa.", "error");
    btn.disabled = false;
  }
}

async function salvarPergunta(pergunta, btnEl) {
  if (!pergunta.enunciado.trim()) {
    UI.toast("Escreva o enunciado da pergunta antes de salvar.", "error");
    return;
  }
  const requerOpcoes = pergunta.tipoPergunta !== "Aberta";
  const opcoesValidas = pergunta.opcoes.filter((o) => o.descricao.trim());
  if (requerOpcoes && opcoesValidas.length < 2) {
    UI.toast("Adicione pelo menos duas opções para esse tipo de pergunta.", "error");
    return;
  }

  btnEl.disabled = true;
  btnEl.textContent = "Salvando...";

  const dto = {
    enunciado: pergunta.enunciado.trim(),
    tipoPergunta: pergunta.tipoPergunta,
    obrigatoria: pergunta.obrigatoria,
    ordem: pergunta.ordem,
    pesquisaId: Number(pesquisaId),
  };

  try {
    if (pergunta.id) {
      await api.perguntas.atualizar(pergunta.id, dto);
    } else {
      const criada = await api.perguntas.salvar(dto);
      pergunta.id = criada.id;
    }
    pergunta._savedOrdem = pergunta.ordem;
    pergunta._dirty = false;

    // Garante que as opções digitadas localmente (sem id) existam no backend.
    for (const opcao of pergunta.opcoes) {
      if (!opcao.descricao.trim()) continue;
      if (!opcao.id) {
        const criada = await api.opcoes.salvar({ descricao: opcao.descricao.trim(), perguntaId: pergunta.id });
        opcao.id = criada.id;
      } else if (opcao._dirty) {
        await api.opcoes.atualizar(opcao.id, { descricao: opcao.descricao.trim(), perguntaId: pergunta.id });
        opcao._dirty = false;
      }
    }

    UI.toast("Pergunta salva.");
    render();
  } catch (err) {
    UI.toast(err.message || "Não foi possível salvar a pergunta.", "error");
    btnEl.disabled = false;
    btnEl.textContent = "Salvar pergunta";
  }
}

function adicionarOpcao(pergunta) {
  pergunta.opcoes.push({ _localId: uid(), id: null, descricao: "" });
  render();
  const card = document.querySelector(`.question-card[data-local-id="${pergunta._localId}"]`);
  card?.querySelectorAll(".campo-opcao-descricao")[pergunta.opcoes.length - 1]?.focus();
}

async function salvarOpcaoExistente(pergunta, opcao) {
  if (!opcao.descricao.trim()) {
    UI.toast("A opção não pode ficar em branco.", "error");
    return;
  }
  try {
    await api.opcoes.atualizar(opcao.id, { descricao: opcao.descricao.trim(), perguntaId: pergunta.id });
    opcao._dirty = false;
    UI.toast("Opção salva.");
  } catch (err) {
    UI.toast(err.message || "Não foi possível salvar a opção.", "error");
  }
}

async function removerOpcao(pergunta, opcao) {
  if (opcao.id) {
    const ok = await UI.confirmar({
      titulo: "Remover opção",
      mensagem: "Respostas que já escolheram essa opção serão afetadas. Deseja continuar?",
      textoConfirmar: "Remover",
      perigoso: true,
    });
    if (!ok) return;
    try {
      await api.opcoes.excluir(opcao.id);
    } catch (err) {
      UI.toast(err.message || "Não foi possível remover a opção.", "error");
      return;
    }
  }
  pergunta.opcoes = pergunta.opcoes.filter((o) => o._localId !== opcao._localId);
  render();
}

async function excluirPergunta(pergunta) {
  if (pergunta.id) {
    const ok = await UI.confirmar({
      titulo: "Excluir pergunta",
      mensagem: "Isso também remove as opções e as respostas já recebidas para essa pergunta.",
      textoConfirmar: "Excluir",
      perigoso: true,
    });
    if (!ok) return;
    try {
      await api.perguntas.excluir(pergunta.id);
    } catch (err) {
      UI.toast(err.message || "Não foi possível excluir a pergunta.", "error");
      return;
    }
  }
  perguntas = perguntas.filter((p) => p._localId !== pergunta._localId);
  await sincronizarOrdem();
  render();
}

async function moverPergunta(pergunta, direcao) {
  const idx = perguntas.findIndex((p) => p._localId === pergunta._localId);
  const novoIdx = idx + direcao;
  if (novoIdx < 0 || novoIdx >= perguntas.length) return;
  [perguntas[idx], perguntas[novoIdx]] = [perguntas[novoIdx], perguntas[idx]];
  await sincronizarOrdem();
  render();
}

/** Recalcula ordem = posição+1 para todas as perguntas e envia PUT só para quem já existe e mudou. */
async function sincronizarOrdem() {
  const updates = [];
  perguntas.forEach((p, idx) => {
    const novaOrdem = idx + 1;
    if (p.ordem !== novaOrdem) {
      p.ordem = novaOrdem;
      if (p.id && p._savedOrdem !== novaOrdem) {
        updates.push(
          api.perguntas
            .atualizar(p.id, {
              enunciado: p.enunciado,
              tipoPergunta: p.tipoPergunta,
              obrigatoria: p.obrigatoria,
              ordem: novaOrdem,
              pesquisaId: Number(pesquisaId),
            })
            .then(() => (p._savedOrdem = novaOrdem))
        );
      }
    }
  });
  if (updates.length) {
    try {
      await Promise.all(updates);
    } catch (err) {
      UI.toast("A ordem pode não ter sido salva por completo: " + err.message, "error");
    }
  }
}