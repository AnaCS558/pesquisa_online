const sessao = Auth.exigirAdmin();
const params = new URLSearchParams(window.location.search);
const pesquisaId = params.get("id");

if (sessao) {
  UI.montarNavbar("pesquisas");
  if (!pesquisaId) {
    erro("Pesquisa não informada.");
  } else {
    carregar();
  }
}

function esc(s) {
  const d = document.createElement("div");
  d.textContent = s ?? "";
  return d.innerHTML;
}

function erro(msg) {
  document.getElementById("content-area").innerHTML = `
    <div class="empty-state">
      <svg class="icon" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
      <h3>Não foi possível carregar os resultados</h3>
      <p>${esc(msg)}</p>
    </div>`;
}

async function carregar() {
  try {
    const [pesquisa, todasRespostas, usuarios] = await Promise.all([
      api.pesquisas.buscarPorId(pesquisaId),
      api.respostas.listar(),
      api.usuarios.listar().catch(() => []), // usado só para exibir nomes nas respostas abertas; segue mesmo se falhar
    ]);

    pesquisa.perguntas = (pesquisa.perguntas || []).slice().sort((a, b) => a.ordem - b.ordem);
    const idsPerguntas = new Set(pesquisa.perguntas.map((p) => p.id));
    const respostas = todasRespostas.filter((r) => idsPerguntas.has(r.perguntaId));
    const nomePorUsuario = new Map(usuarios.map((u) => [u.id, u.nome]));

    render(pesquisa, respostas, nomePorUsuario);
  } catch (err) {
    erro(err.message);
  }
}

function render(pesquisa, respostas, nomePorUsuario) {
  const participantes = new Set(respostas.map((r) => r.usuarioId)).size;
  const totalPerguntas = pesquisa.perguntas.length;

  document.getElementById("content-area").innerHTML = `
    <div class="page-head" style="margin-bottom:1.25rem;">
      <h1>${esc(pesquisa.titulo)}</h1>
      <span class="badge ${pesquisa.ativa ? "badge--active" : "badge--closed"}">${pesquisa.ativa ? "Ativa" : "Encerrada"}</span>
    </div>

    <dl class="results-summary">
      <div class="card stat-tile"><dt>Participantes</dt><dd>${participantes}</dd></div>
      <div class="card stat-tile"><dt>Respostas registradas</dt><dd>${respostas.length}</dd></div>
      <div class="card stat-tile"><dt>Perguntas</dt><dd>${totalPerguntas}</dd></div>
      <div class="card stat-tile"><dt>Criada em</dt><dd style="font-size:1rem;">${UI.formatarData(pesquisa.dataCriacao)}</dd></div>
    </dl>

    ${
      totalPerguntas
        ? pesquisa.perguntas.map((p) => renderPerguntaResultado(p, respostas.filter((r) => r.perguntaId === p.id), nomePorUsuario)).join("")
        : `<div class="empty-state"><p>Esta pesquisa ainda não tem perguntas.</p></div>`
    }
  `;
}

function renderPerguntaResultado(p, respostasDaPergunta, nomePorUsuario) {
  const totalRespondentes = new Set(respostasDaPergunta.map((r) => r.usuarioId)).size;

  let corpo;
  if (p.tipoPergunta === "Aberta") {
    corpo = respostasDaPergunta.length
      ? `<div class="result-text-list">
          ${respostasDaPergunta
            .map(
              (r) => `
            <div class="result-text-item">
              <p>${esc(r.respostaTexto || "—")}</p>
              <span>${esc(nomePorUsuario.get(r.usuarioId) || "Participante")} · ${UI.formatarDataHora(r.dataResposta)}</span>
            </div>`
            )
            .join("")}
        </div>`
      : semRespostas();
  } else {
    const contagem = new Map((p.opcoes || []).map((o) => [o.id, 0]));
    respostasDaPergunta.forEach((r) => {
      if (contagem.has(r.opcaoId)) contagem.set(r.opcaoId, contagem.get(r.opcaoId) + 1);
    });
    const maxContagem = Math.max(1, ...contagem.values());
    corpo = (p.opcoes || []).length
      ? (p.opcoes || [])
          .map((o) => {
            const n = contagem.get(o.id) || 0;
            const pct = totalRespondentes ? Math.round((n / totalRespondentes) * 100) : 0;
            const largura = Math.round((n / maxContagem) * 100);
            return `
            <div class="result-bar">
              <div class="result-bar__label"><span>${esc(o.descricao)}</span><span>${n} (${pct}%)</span></div>
              <div class="result-bar__track"><div class="result-bar__fill" style="width:${largura}%"></div></div>
            </div>`;
          })
          .join("")
      : semRespostas();
  }

  return `
    <section class="card result-card">
      <div class="result-card__head">
        <div>
          <h3>${esc(p.enunciado)}</h3>
          <span class="field__hint">${UI.rotuloTipoPergunta(p.tipoPergunta)}</span>
        </div>
        <span class="result-card__count">${totalRespondentes} ${totalRespondentes === 1 ? "resposta" : "respostas"}</span>
      </div>
      ${corpo}
    </section>`;
}

function semRespostas() {
  return `<p class="field__hint">Ainda não há respostas para esta pergunta.</p>`;
}