package com.exemplo.usuario.ranking.application;

import com.exemplo.usuario.ranking.api.RankingResponseDTO;
import com.exemplo.usuario.ranking.internal.persistence.RankingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RankingService {
    private final RankingRepository repository;

    public RankingService(RankingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RankingResponseDTO> listar() {
        return repository.buscarRanking().stream().map(a -> new RankingResponseDTO(
                a.getUsuario().getId(), a.getUsuario().getNome(), a.getCursosConcluidosComSucesso(), a.getMoedas(),
                a.getCursosConcluidosComSucesso() * 100 + a.getMoedas())).toList();
    }
}
