/**
 * api.js — camada de acesso à API REST do backend Pesquisa Online (Spring Boot).
 *
 * IMPORTANTE — ajuste antes de usar:
 * 1) API_BASE abaixo precisa apontar para onde o backend Spring Boot está
 *    rodando (por padrão, Spring Boot sobe em http://localhost:8080).
 * 2) Os controllers do backend (PesquisaController, UsuarioController, etc.)
 *    não têm um prefixo comum como "/api" — cada um mapeia direto na raiz
 *    (ex.: @RequestMapping("/pesquisas")). Se isso mudar no backend, ajuste
 *    os caminhos aqui.
 * 3) O backend precisa liberar CORS para a origem de onde este frontend é
 *    servido (ex.: http://127.0.0.1:5500 se você usar o Live Server do
 *    VS Code). Sem isso, toda chamada vai falhar com erro de rede no
 *    console, mesmo que o backend esteja no ar. Exemplo mínimo de
 *    configuração no Spring Boot:
 *
 *    @Configuration
 *    public class CorsConfig implements WebMvcConfigurer {
 *        @Override
 *        public void addCorsMappings(CorsRegistry registry) {
 *            registry.addMapping("/**")
 *                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500")
 *                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
 *        }
 *    }
 */

const API_BASE = "http://localhost:8080";

/**
 * Faz o fetch, trata erros HTTP e devolve o corpo já em JSON.
 * Lança um Error com uma mensagem amigável (tenta ler {message} ou
 * {erro} do corpo de erro, já que o backend não expôs seu
 * @ControllerAdvice/GlobalExceptionHandler nos arquivos recebidos).
 */
async function request(path, options = {}) {
  let res;
  try {
    res = await fetch(`${API_BASE}${path}`, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
  } catch (networkError) {
    throw new Error(
      "Não foi possível conectar à API. Verifique se o backend está rodando em " +
        API_BASE +
        " e se o CORS está liberado para esta origem."
    );
  }

  if (res.status === 204) return null;

  let body = null;
  const text = await res.text();
  if (text) {
    try {
      body = JSON.parse(text);
    } catch {
      body = text;
    }
  }

  if (!res.ok) {
    const msg =
      (body && (body.message || body.erro || body.error)) ||
      (typeof body === "string" && body) ||
      `Erro ${res.status} ao chamar ${path}.`;
    const err = new Error(msg);
    err.status = res.status;
    err.body = body;
    throw err;
  }

  return body;
}

const api = {
  usuarios: {
    listar: () => request("/usuarios"),
    buscarPorId: (id) => request(`/usuarios/${id}`),
    buscarPorEmail: (email) =>
      request(`/usuarios/email/${encodeURIComponent(email)}`),
    salvar: (dto) =>
      request("/usuarios", { method: "POST", body: JSON.stringify(dto) }),
    atualizar: (id, dto) =>
      request(`/usuarios/${id}`, { method: "PUT", body: JSON.stringify(dto) }),
    excluir: (id) => request(`/usuarios/${id}`, { method: "DELETE" }),
  },

  pesquisas: {
    listar: () => request("/pesquisas"),
    buscarPorId: (id) => request(`/pesquisas/${id}`),
    salvar: (dto) =>
      request("/pesquisas", { method: "POST", body: JSON.stringify(dto) }),
    atualizar: (id, dto) =>
      request(`/pesquisas/${id}`, { method: "PUT", body: JSON.stringify(dto) }),
    excluir: (id) => request(`/pesquisas/${id}`, { method: "DELETE" }),
  },

  perguntas: {
    listar: () => request("/perguntas"),
    salvar: (dto) =>
      request("/perguntas", { method: "POST", body: JSON.stringify(dto) }),
    atualizar: (id, dto) =>
      request(`/perguntas/${id}`, { method: "PUT", body: JSON.stringify(dto) }),
    excluir: (id) => request(`/perguntas/${id}`, { method: "DELETE" }),
  },

  opcoes: {
    listar: () => request("/opcoes"),
    buscarPorId: (id) => request(`/opcoes/${id}`),
    salvar: (dto) =>
      request("/opcoes", { method: "POST", body: JSON.stringify(dto) }),
    atualizar: (id, dto) =>
      request(`/opcoes/${id}`, { method: "PUT", body: JSON.stringify(dto) }),
    excluir: (id) => request(`/opcoes/${id}`, { method: "DELETE" }),
  },

  respostas: {
    listar: () => request("/respostas"),
    salvar: (dto) =>
      request("/respostas", { method: "POST", body: JSON.stringify(dto) }),
    atualizar: (id, dto) =>
      request(`/respostas/${id}`, { method: "PUT", body: JSON.stringify(dto) }),
    excluir: (id) => request(`/respostas/${id}`, { method: "DELETE" }),
  },
};