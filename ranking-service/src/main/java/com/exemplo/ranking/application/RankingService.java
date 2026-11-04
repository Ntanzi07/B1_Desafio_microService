package com.exemplo.ranking.application;

import com.exemplo.ranking.api.RankingResponseDTO;
import com.exemplo.ranking.api.RankingUpdateRequestDTO;
import com.exemplo.ranking.domain.RankingEntry;
import com.exemplo.ranking.internal.persistence.RankingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Camada: SERVICE (casos de uso do ranking).
@Service
public class RankingService {

    private final RankingRepository repository;

    public RankingService(RankingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RankingResponseDTO> listar() {
        return repository.findAllByOrderByPontosDescUsuarioIdAsc().stream()
                .map(RankingResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RankingResponseDTO buscar(Long usuarioId) {
        return repository.findById(usuarioId)
                .map(RankingResponseDTO::from)
                .orElseThrow(() -> new RankingNaoEncontradoException(usuarioId));
    }

    // UPSERT: se a linha existe, atualiza; se nao existe, cria.
    // Mandar o mesmo PUT duas vezes deixa o banco igual (idempotente),
    // entao o Core pode repetir a chamada com seguranca se a rede falhar.
    @Transactional
    public RankingResponseDTO salvar(Long usuarioId, RankingUpdateRequestDTO dto) {
        RankingEntry entry = repository.findById(usuarioId)
                .orElseGet(() -> new RankingEntry(usuarioId));
        entry.atualizar(dto.nome(), dto.cursosConcluidos(), dto.moedas());
        return RankingResponseDTO.from(repository.save(entry));
    }

    // Tambem idempotente: apagar algo que ja nao existe nao e erro.
    // (No Spring Data 3, deleteById ignora id inexistente.)
    @Transactional
    public void remover(Long usuarioId) {
        repository.deleteById(usuarioId);
    }
}
