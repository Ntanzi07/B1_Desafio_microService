package com.exemplo.ranking.application;

import com.exemplo.ranking.api.RankingResponseDTO;
import com.exemplo.ranking.api.RankingUpdateRequestDTO;
import com.exemplo.ranking.domain.RankingEntry;
import com.exemplo.ranking.internal.persistence.RankingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Teste de SERVICE com Mockito: o repository e falso, testamos so a logica do caso de uso.
@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private RankingRepository repository;

    private RankingService service;

    @BeforeEach
    void setUp() {
        service = new RankingService(repository);
    }

    @Test
    void salvarCriaLinhaQuandoUsuarioAindaNaoEstaNoRanking() {
        when(repository.findById(7L)).thenReturn(Optional.empty());
        when(repository.save(any(RankingEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        RankingResponseDTO resposta = service.salvar(7L, new RankingUpdateRequestDTO("Ana", 2, 30));

        ArgumentCaptor<RankingEntry> salvo = ArgumentCaptor.forClass(RankingEntry.class);
        verify(repository).save(salvo.capture());
        assertEquals(7L, salvo.getValue().getUsuarioId());
        assertEquals(230, resposta.pontos());
    }

    @Test
    void salvarAtualizaLinhaExistenteSemCriarOutra() {
        RankingEntry existente = new RankingEntry(7L);
        existente.atualizar("Ana", 1, 0);
        when(repository.findById(7L)).thenReturn(Optional.of(existente));
        when(repository.save(any(RankingEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        RankingResponseDTO resposta = service.salvar(7L, new RankingUpdateRequestDTO("Ana Souza", 3, 50));

        // A mesma instancia foi atualizada (upsert), nao uma nova.
        verify(repository).save(existente);
        assertEquals("Ana Souza", resposta.nome());
        assertEquals(350, resposta.pontos());
    }

    @Test
    void listarMantemAOrdemDoRepositorio() {
        RankingEntry primeiro = new RankingEntry(1L);
        primeiro.atualizar("Bia", 3, 0);
        RankingEntry segundo = new RankingEntry(2L);
        segundo.atualizar("Ana", 1, 10);
        when(repository.findAllByOrderByPontosDescUsuarioIdAsc()).thenReturn(List.of(primeiro, segundo));

        List<RankingResponseDTO> ranking = service.listar();

        assertEquals(2, ranking.size());
        assertEquals("Bia", ranking.get(0).nome());
        assertEquals(300, ranking.get(0).pontos());
        assertEquals("Ana", ranking.get(1).nome());
    }

    @Test
    void buscarUsuarioInexistenteLancaExcecaoDeNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RankingNaoEncontradoException.class, () -> service.buscar(99L));
    }

    @Test
    void removerDelegaParaORepository() {
        service.remover(7L);

        verify(repository).deleteById(7L);
    }
}
