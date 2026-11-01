package com.ana.pesquisa_online.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

    @GetMapping("/login.html")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro.html")
    public String cadastro() {
        return "cadastro";
    }

    @GetMapping("/pesquisas.html")
    public String pesquisas() {
        return "pesquisas";
    }

    @GetMapping("/editor.html")
    public String editor() {
        return "editor";
    }

    @GetMapping("/responder.html")
    public String responder() {
        return "responder";
    }

    @GetMapping("/resultados.html")
    public String resultados() {
        return "resultados";
    }
}