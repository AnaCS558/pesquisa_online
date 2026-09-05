package com.ana.pesquisa_online.controller;

import com.ana.pesquisa_online.dto.PerguntaRequestDTO;
import com.ana.pesquisa_online.dto.PerguntaResponseDTO;
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
    public List<PerguntaResponseDTO> listarTodas() {
        return perguntaService.listarTodas();
    }

    @PostMapping
    public PerguntaResponseDTO salvar(
            @Valid @RequestBody PerguntaRequestDTO dto) {

        return perguntaService.salvar(dto);
    }

    @PutMapping("/{id}")
    public PerguntaResponseDTO atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PerguntaRequestDTO dto) {

        return perguntaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        perguntaService.excluir(id);
    }
}