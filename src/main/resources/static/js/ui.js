/**
 * ui.js — helpers de interface compartilhados entre as páginas:
 * navbar com menu do usuário, toasts, modal de confirmação e formatação.
 */

const UI = {
  /** Monta a navbar padrão dentro do elemento #navbar-slot da página. */
  montarNavbar(paginaAtiva) {
    const slot = document.getElementById("navbar-slot");
    if (!slot) return;
    const sessao = Auth.getSessao();
    if (!sessao) return;

    const inicial = (sessao.nome || "?").trim().charAt(0).toUpperCase();
    const links = [{ id: "pesquisas", label: "Pesquisas", href: "pesquisas.html" }];

    slot.innerHTML = `
      <header class="navbar">
        <a class="navbar__brand" href="pesquisas.html">
          <span class="navbar__mark">P</span>
          <span>Pesquisa Online</span>
        </a>
        <nav class="navbar__center">
          ${links
            .map(
              (l) => `
            <a class="btn btn--sm ${l.id === paginaAtiva ? "btn--outline" : "btn--ghost"}" href="${l.href}">${l.label}</a>
          `
            )
            .join("")}
        </nav>
        <div class="navbar__user">
          <div class="navbar__info">
            <strong>${escapeHtml(sessao.nome)}</strong>
            <span>${sessao.perfil === "Admin" ? "Administrador" : "Participante"}</span>
          </div>
          <button class="navbar__avatar" id="navbar-avatar-btn" aria-haspopup="true" aria-expanded="false">${inicial}</button>
          <div class="navbar__menu" id="navbar-menu">
            <button type="button" id="navbar-logout-btn">Sair</button>
          </div>
        </div>
      </header>
    `;

    const btn = document.getElementById("navbar-avatar-btn");
    const menu = document.getElementById("navbar-menu");
    btn.addEventListener("click", (e) => {
      e.stopPropagation();
      menu.classList.toggle("is-open");
    });
    document.addEventListener("click", () => menu.classList.remove("is-open"));
    document.getElementById("navbar-logout-btn").addEventListener("click", () => Auth.sair());
  },

  toast(mensagem, tipo = "success") {
    let stack = document.querySelector(".toast-stack");
    if (!stack) {
      stack = document.createElement("div");
      stack.className = "toast-stack";
      document.body.appendChild(stack);
    }
    const el = document.createElement("div");
    el.className = `toast toast--${tipo}`;
    el.innerHTML = `${tipo === "error" ? iconSvg("alert") : iconSvg("check")}<span>${escapeHtml(mensagem)}</span>`;
    stack.appendChild(el);
    setTimeout(() => el.remove(), 4200);
  },

  /** Modal de confirmação simples. Retorna uma Promise<boolean>. */
  confirmar({ titulo, mensagem, textoConfirmar = "Confirmar", perigoso = false }) {
    return new Promise((resolve) => {
      let overlay = document.getElementById("confirm-overlay");
      if (!overlay) {
        overlay = document.createElement("div");
        overlay.id = "confirm-overlay";
        overlay.className = "modal-overlay";
        document.body.appendChild(overlay);
      }
      overlay.innerHTML = `
        <div class="modal" role="alertdialog" aria-modal="true">
          <h3>${escapeHtml(titulo)}</h3>
          <p>${escapeHtml(mensagem)}</p>
          <div class="modal__actions">
            <button class="btn btn--ghost" id="confirm-cancel">Cancelar</button>
            <button class="btn ${perigoso ? "btn--danger" : "btn--primary"}" id="confirm-ok">${escapeHtml(textoConfirmar)}</button>
          </div>
        </div>
      `;
      overlay.classList.add("is-open");

      const close = (result) => {
        overlay.classList.remove("is-open");
        resolve(result);
      };
      overlay.querySelector("#confirm-cancel").addEventListener("click", () => close(false));
      overlay.querySelector("#confirm-ok").addEventListener("click", () => close(true));
      overlay.addEventListener("click", (e) => {
        if (e.target === overlay) close(false);
      });
    });
  },

  formatarData(isoString) {
    if (!isoString) return "—";
    const d = new Date(isoString);
    if (isNaN(d.getTime())) return "—";
    return d.toLocaleDateString("pt-BR", { day: "2-digit", month: "short", year: "numeric" });
  },

  formatarDataHora(isoString) {
    if (!isoString) return "—";
    const d = new Date(isoString);
    if (isNaN(d.getTime())) return "—";
    return d.toLocaleString("pt-BR", {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  },

  rotuloTipoPergunta(tipo) {
    return (
      { Aberta: "Resposta curta", Unica_Escolha: "Múltipla escolha (uma opção)", Multipla_Escolha: "Caixas de seleção (várias opções)" }[
        tipo
      ] || tipo
    );
  },
};

function escapeHtml(str) {
  return String(str ?? "").replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

function iconSvg(name) {
  const paths = {
    check: '<polyline points="20 6 9 17 4 12"/>',
    alert: '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>',
  };
  return `<svg class="icon" viewBox="0 0 24 24" aria-hidden="true">${paths[name] || ""}</svg>`;
}