package com.ana.pesquisa_online.controller;

import com.ana.pesquisa_online.entity.Resposta;
import com.ana.pesquisa_online.service.RespostaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/respostas")
public class RespostaController {

    private final RespostaService respostaService;

    public RespostaController(RespostaService respostaService) {
        this.respostaService = respostaService;
    }

    @GetMapping
    public List<Resposta> listarTodas() {
        return respostaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Resposta buscarPorId(@PathVariable Long id) {
        return respostaService.buscarPorId(id);
    }

    @PostMapping
    public Resposta salvar(@Valid @RequestBody Resposta resposta) {
        return respostaService.salvar(resposta);
    }

    @PutMapping("/{id}")
    public Resposta atualizar(@PathVariable Long id,
                              @Valid @RequestBody Resposta resposta) {
        return respostaService.atualizar(id, resposta);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        respostaService.excluir(id);
    }
}
