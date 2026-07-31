package com.ana.pesquisa_online.controller;

import com.ana.pesquisa_online.entity.Pesquisa;
import com.ana.pesquisa_online.service.PesquisaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pesquisas")
public class PesquisaController {

    private final PesquisaService pesquisaService;

    public PesquisaController(PesquisaService pesquisaService) {
        this.pesquisaService = pesquisaService;
    }

    @GetMapping
    public List<Pesquisa> listarTodas() {
        return pesquisaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Pesquisa buscarPorId(@PathVariable Long id) {
        return pesquisaService.buscarPorId(id);
    }

    @PostMapping
    public Pesquisa salvar(@Valid @RequestBody Pesquisa pesquisa) {
        return pesquisaService.salvar(pesquisa);
    }

    @PutMapping("/{id}")
    public Pesquisa atualizar(@PathVariable Long id,
                             @Valid @RequestBody Pesquisa pesquisa) {

        return pesquisaService.atualizar(id, pesquisa);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        pesquisaService.excluir(id);
    }
}
