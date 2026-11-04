package com.exemplo.usuario.matricula.application;

import com.exemplo.usuario.usuario.domain.Assinatura;
import com.exemplo.usuario.curso.domain.Curso;
import com.exemplo.usuario.matricula.domain.Matricula;
import com.exemplo.usuario.matricula.domain.StatusMatricula;
import com.exemplo.usuario.usuario.domain.Usuario;
import com.exemplo.usuario.matricula.api.MatriculaResponseDTO;
import com.exemplo.usuario.usuario.internal.persistence.AssinaturaRepository;
import com.exemplo.usuario.curso.internal.persistence.CursoRepository;
import com.exemplo.usuario.matricula.internal.persistence.MatriculaRepository;
import com.exemplo.usuario.usuario.internal.persistence.UsuarioRepository;
import com.exemplo.usuario.integracao.ranking.RankingDadosAlteradosEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Camada: SERVICE.
// Esta classe orquestra o caso de uso de matricula.
@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final ApplicationEventPublisher eventos;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            UsuarioRepository usuarioRepository,
                            CursoRepository cursoRepository,
                            AssinaturaRepository assinaturaRepository,
                            ApplicationEventPublisher eventos) {
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.assinaturaRepository = assinaturaRepository;
        this.eventos = eventos;
    }

    public List<MatriculaResponseDTO> listarPorUsuario(Long usuarioId) {
        return matriculaRepository.findByUsuarioId(usuarioId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public MatriculaResponseDTO matricular(Long usuarioId, Long cursoId, boolean bonus) {
        // 1) Buscar as entidades necessarias.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso nao encontrado"));

        Assinatura assinatura = assinaturaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Assinatura nao encontrada"));

        // 2) Aplicar regra de negocio.
        if (bonus) {
            assinatura.consumirCredito();
        }

        // 3) Criar entidade do dominio.
        Matricula matricula = new Matricula(usuario, curso, bonus);

        // 4) Persistir e devolver DTO.
        return toDTO(matriculaRepository.save(matricula));
    }

    @Transactional
    public MatriculaResponseDTO concluir(Long matriculaId, Double notaFinal) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada"));

        // Regras de atualizacao do estado da matricula.
        matricula.setStatus(StatusMatricula.CONCLUIDO);
        matricula.setNotaFinal(notaFinal);

        // Se houve aproveitamento, a assinatura do usuario e atualizada.
        if (matricula.concluidoComAproveitamento()) {
            Assinatura assinatura = assinaturaRepository.findByUsuarioId(matricula.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Assinatura nao encontrada"));
            assinatura.registrarConclusaoComSucesso();

            // Cursos concluidos mudou -> o ranking desse usuario mudou.
            // O envio ao Ranking Service so acontece apos o commit desta transacao.
            eventos.publishEvent(new RankingDadosAlteradosEvent(
                    matricula.getUsuario().getId(), matricula.getUsuario().getNome(),
                    assinatura.getCursosConcluidosComSucesso(), assinatura.getMoedas()));
        }

        return toDTO(matriculaRepository.save(matricula));
    }

    private MatriculaResponseDTO toDTO(Matricula matricula) {
        return new MatriculaResponseDTO(
                matricula.getId(),
                matricula.getUsuario().getId(),
                matricula.getUsuario().getNome(),
                matricula.getCurso().getId(),
                matricula.getCurso().getTitulo(),
                matricula.getStatus().name(),
                matricula.getNotaFinal(),
                matricula.isBonus()
        );
    }
}
