package com.ana.pesquisa_online.controller;

import com.ana.pesquisa_online.dto.PesquisaRequestDTO;
import com.ana.pesquisa_online.dto.PesquisaResponseDTO;
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
    public List<PesquisaResponseDTO> listarTodas() {
        return pesquisaService.listarTodas();
    }

    @GetMapping("/{id}")
    public PesquisaResponseDTO buscarPorId(@PathVariable Long id) {
        return pesquisaService.buscarPorId(id);
    }

    @PostMapping
    public PesquisaResponseDTO salvar(
            @Valid @RequestBody PesquisaRequestDTO dto) {

        return pesquisaService.salvar(dto);
    }

    @PutMapping("/{id}")
    public PesquisaResponseDTO atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PesquisaRequestDTO dto) {

        return pesquisaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        pesquisaService.excluir(id);
    }
}