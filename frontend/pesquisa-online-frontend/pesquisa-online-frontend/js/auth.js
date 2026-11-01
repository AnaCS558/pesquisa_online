/**
 * auth.js — sessão do usuário no navegador.
 *
 * LIMITAÇÃO CONHECIDA (leia antes de usar em produção):
 * O backend recebido não tem um endpoint de login (ex.: POST /auth/login) e
 * o UsuarioResponseDTO não devolve a senha (correto, por segurança — nunca
 * deveria devolver mesmo). Isso significa que, com a API atual, não existe
 * como validar a senha no login: este frontend busca o usuário por e-mail
 * (GET /usuarios/email/{email}) e, se ele existir, considera a sessão
 * iniciada — sem checar a senha digitada.
 *
 * Para um login de verdade, o backend precisa de um endpoint dedicado
 * (ex.: POST /auth/login recebendo email+senha, validando com
 * PasswordEncoder e devolvendo um token). Quando esse endpoint existir,
 * troque apenas a função `login()` abaixo — o resto do app não muda.
 */

const SESSION_KEY = "pesquisaOnline.sessao";

const Auth = {
  /** Lê a sessão atual (ou null se deslogado). */
  getSessao() {
    try {
      const raw = localStorage.getItem(SESSION_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  },

  salvarSessao(usuario) {
    localStorage.setItem(
      SESSION_KEY,
      JSON.stringify({
        id: usuario.id,
        nome: usuario.nome,
        email: usuario.email,
        perfil: usuario.perfil,
      })
    );
  },

  sair() {
    localStorage.removeItem(SESSION_KEY);
    window.location.href = "login.html";
  },

  estaLogado() {
    return !!this.getSessao();
  },

  ehAdmin() {
    return this.getSessao()?.perfil === "Admin";
  },

  /** Redireciona para o login se não houver sessão. Use no topo de cada página protegida. */
  exigirSessao() {
    const sessao = this.getSessao();
    if (!sessao) {
      window.location.href = "login.html";
      return null;
    }
    return sessao;
  },

  /** Redireciona para o dashboard se a página for exclusiva de Admin e o usuário não for. */
  exigirAdmin() {
    const sessao = this.exigirSessao();
    if (sessao && sessao.perfil !== "Admin") {
      window.location.href = "pesquisas.html";
      return null;
    }
    return sessao;
  },
};
