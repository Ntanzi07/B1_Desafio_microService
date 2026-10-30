package com.exemplo.usuario.curso.application;

import com.exemplo.usuario.curso.domain.Curso;
import com.exemplo.usuario.curso.api.CursoRequestDTO;
import com.exemplo.usuario.curso.api.CursoResponseDTO;
import com.exemplo.usuario.curso.internal.persistence.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Camada: SERVICE.
// Service concentra regras de negocio e orquestracao do caso de uso.
@Service
public class CursoService {

    private final CursoRepository repository;

    // Injeção de dependência por construtor.
    // O bean de CursoRepository e entregue pelo Spring.
    public CursoService(CursoRepository repository) {
        this.repository = repository;
    }

    public List<CursoResponseDTO> listarTodos() {
        // findAll() vem do JpaRepository.
        // map(this::toDTO) converte entidade em DTO de saida.
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    // @Transactional e tipico da camada de servico.
    // Indica que o metodo deve executar dentro de uma transacao.
    @Transactional
    public CursoResponseDTO criar(CursoRequestDTO dto) {
        // Aqui o service instancia a entidade de dominio.
        Curso curso = new Curso(dto.getTitulo(), dto.getDescricao());

        // save(...) persiste no banco.
        return toDTO(repository.save(curso));
    }

    // Metodo privado de apoio para mapear entidade -> DTO.
    private CursoResponseDTO toDTO(Curso curso) {
        return new CursoResponseDTO(curso.getId(), curso.getTitulo(), curso.getDescricao());
    }
}
