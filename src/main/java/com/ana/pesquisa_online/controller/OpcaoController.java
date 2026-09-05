package com.ana.pesquisa_online.controller;

import com.ana.pesquisa_online.dto.OpcaoRequestDTO;
import com.ana.pesquisa_online.dto.OpcaoResponseDTO;
import com.ana.pesquisa_online.service.OpcaoService;
import jakarta.validation.Valid;
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
    public List<OpcaoResponseDTO> listarTodas() {
        return opcaoService.listarTodas();
    }

    @GetMapping("/{id}")
    public OpcaoResponseDTO buscarPorId(@PathVariable Long id) {
        return opcaoService.buscarPorId(id);
    }

    @PostMapping
    public OpcaoResponseDTO salvar(
            @Valid @RequestBody OpcaoRequestDTO dto) {

        return opcaoService.salvar(dto);
    }

    @PutMapping("/{id}")
    public OpcaoResponseDTO atualizar(
            @PathVariable Long id,
            @Valid @RequestBody OpcaoRequestDTO dto) {

        return opcaoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        opcaoService.excluir(id);
    }
}