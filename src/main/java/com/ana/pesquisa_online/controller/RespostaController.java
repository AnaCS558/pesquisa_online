package com.ana.pesquisa_online.controller;

import com.ana.pesquisa_online.dto.RespostaRequestDTO;
import com.ana.pesquisa_online.dto.RespostaResponseDTO;
import com.ana.pesquisa_online.service.RespostaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<RespostaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(respostaService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<RespostaResponseDTO> salvar(
            @Valid @RequestBody RespostaRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respostaService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespostaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RespostaRequestDTO dto) {

        return ResponseEntity.ok(respostaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        respostaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}