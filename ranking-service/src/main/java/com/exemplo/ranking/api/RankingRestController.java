package com.exemplo.ranking.api;

import com.exemplo.ranking.application.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Camada: API.
// Quem pode chamar o que esta no SecurityConfig:
// GET e publico; PUT e DELETE exigem a credencial de servico (o Core).
@RestController
@RequestMapping("/api/ranking")
@Tag(name = "Ranking")
public class RankingRestController {

    private final RankingService service;

    public RankingRestController(RankingService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Ranking ordenado por pontos (100 por curso concluido + moedas)")
    public List<RankingResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Posicao de um usuario no ranking")
    public RankingResponseDTO buscar(@PathVariable Long usuarioId) {
        return service.buscar(usuarioId);
    }

    @PutMapping("/{usuarioId}")
    @Operation(summary = "Cria ou atualiza a linha do usuario (uso do Core)")
    public RankingResponseDTO salvar(@PathVariable Long usuarioId,
                                     @Valid @RequestBody RankingUpdateRequestDTO dto) {
        return service.salvar(usuarioId, dto);
    }

    @DeleteMapping("/{usuarioId}")
    @Operation(summary = "Remove o usuario do ranking (uso do Core)")
    public ResponseEntity<Void> remover(@PathVariable Long usuarioId) {
        service.remover(usuarioId);
        return ResponseEntity.noContent().build(); // 204
    }
}
