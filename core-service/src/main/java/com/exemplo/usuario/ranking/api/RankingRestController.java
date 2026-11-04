package com.exemplo.usuario.ranking.api;

import com.exemplo.usuario.ranking.api.RankingResponseDTO;
import com.exemplo.usuario.ranking.application.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@Tag(name = "Ranking")
public class RankingRestController {
    private final RankingService service;

    public RankingRestController(RankingService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Ranking por pontos: 100 por curso concluido + moedas")
    public List<RankingResponseDTO> listar() {
        return service.listar();
    }
}
