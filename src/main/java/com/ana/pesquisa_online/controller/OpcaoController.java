package com.ana.pesquisa_online.controller;

import com.ana.pesquisa_online.entity.Opcao;
import com.ana.pesquisa_online.service.OpcaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/opcoes")
public class OpcaoController {

    private final OpcaoService opcaoService;

    public OpcaoController(OpcaoService opcaoService) {
        this.opcaoService = opcaoService;
    }

    @GetMapping
    public List<Opcao> listarTodas() {
        return opcaoService.listarTodas();
    }

    @GetMapping("/{id}")
    public Opcao buscarPorId(@PathVariable Long id) {
        return opcaoService.buscarPorId(id);
    }

    @PostMapping
    public Opcao salvar(@RequestBody Opcao opcao) {
        return opcaoService.salvar(opcao);
    }

    @PutMapping("/{id}")
    public Opcao atualizar(@PathVariable Long id,
                           @RequestBody Opcao opcao) {

        return opcaoService.atualizar(id, opcao);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        opcaoService.excluir(id);
    }
}
