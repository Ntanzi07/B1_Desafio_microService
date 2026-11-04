package com.exemplo.usuario.matricula.application;

import com.exemplo.usuario.curso.domain.Curso;
import com.exemplo.usuario.curso.internal.persistence.CursoRepository;
import com.exemplo.usuario.integracao.ranking.RankingDadosAlteradosEvent;
import com.exemplo.usuario.matricula.domain.Matricula;
import com.exemplo.usuario.matricula.internal.persistence.MatriculaRepository;
import com.exemplo.usuario.usuario.domain.Assinatura;
import com.exemplo.usuario.usuario.domain.Usuario;
import com.exemplo.usuario.usuario.internal.persistence.AssinaturaRepository;
import com.exemplo.usuario.usuario.internal.persistence.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Prova que a conclusao de matricula aplica as recompensas e AVISA o Ranking
// (publica RankingDadosAlteradosEvent com a foto correta), sem precisar do Ranking no ar.
@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock private MatriculaRepository matriculaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CursoRepository cursoRepository;
    @Mock private AssinaturaRepository assinaturaRepository;
    @Mock private ApplicationEventPublisher eventos;

    private MatriculaService service;
    private Usuario usuario;
    private Assinatura assinatura;
    private Matricula matricula;

    @BeforeEach
    void setUp() {
        service = new MatriculaService(matriculaRepository, usuarioRepository, cursoRepository,
                assinaturaRepository, eventos);

        usuario = new Usuario("Ana", "ana@email.com", "hash");
        // O id normalmente e gerado pelo banco; no teste, colocamos na mao.
        ReflectionTestUtils.setField(usuario, "id", 10L);
        assinatura = new Assinatura(usuario);
        matricula = new Matricula(usuario, new Curso("Java", "Basico"), false);

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void notaDezDaCursoMaisCinquentaMoedasEAvisaORanking() {
        when(assinaturaRepository.findByUsuarioId(10L)).thenReturn(Optional.of(assinatura));

        service.concluir(1L, 10.0);

        assertEquals(1, assinatura.getCursosConcluidosComSucesso());
        assertEquals(50, assinatura.getMoedas());

        RankingDadosAlteradosEvent evento = eventoPublicado();
        assertEquals(10L, evento.usuarioId());
        assertEquals("Ana", evento.nome());
        assertEquals(1, evento.cursosConcluidos());
        assertEquals(50, evento.moedas()); // a foto ja vai com as moedas novas
    }

    @Test
    void notaOitoDaCursoSemMoedasEAvisaORanking() {
        when(assinaturaRepository.findByUsuarioId(10L)).thenReturn(Optional.of(assinatura));

        service.concluir(1L, 8.0);

        assertEquals(0, assinatura.getMoedas());
        RankingDadosAlteradosEvent evento = eventoPublicado();
        assertEquals(1, evento.cursosConcluidos());
        assertEquals(0, evento.moedas());
    }

    @Test
    void notaBaixaNaoMudaORankingENaoPublicaEvento() {
        service.concluir(1L, 5.0);

        verifyNoInteractions(assinaturaRepository);
        // any(Object.class) garante que estamos verificando publishEvent(Object),
        // a sobrecarga que o service usa.
        verify(eventos, never()).publishEvent(any(Object.class));
    }

    private RankingDadosAlteradosEvent eventoPublicado() {
        ArgumentCaptor<RankingDadosAlteradosEvent> captor = ArgumentCaptor.forClass(RankingDadosAlteradosEvent.class);
        verify(eventos).publishEvent(captor.capture());
        return captor.getValue();
    }
}
