package com.ana.pesquisa_online.controller;

import com.ana.pesquisa_online.entity.Pergunta;
import com.ana.pesquisa_online.service.PerguntaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/perguntas")
public class PerguntaController {

    private final PerguntaService perguntaService;

    public PerguntaController(PerguntaService perguntaService) {
        this.perguntaService = perguntaService;
    }

    @GetMapping
    public List<Pergunta> listarTodas() {
        return perguntaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Pergunta buscarPorId(@PathVariable Long id) {
        return perguntaService.buscarPorId(id);
    }

    @PostMapping
    public Pergunta salvar(@Valid @RequestBody Pergunta pergunta) {
        return perguntaService.salvar(pergunta);
    }

    @PutMapping("/{id}")
    public Pergunta atualizar(@PathVariable Long id,
                              @Valid @RequestBody Pergunta pergunta) {
        return perguntaService.atualizar(id, pergunta);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        perguntaService.excluir(id);
    }
}

