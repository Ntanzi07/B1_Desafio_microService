package com.exemplo.usuario.curso.internal.persistence;

import com.exemplo.usuario.curso.domain.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository de Curso.
// Herdamos varios metodos prontos como:
// - findAll()
// - findById(...)
// - save(...)
// - deleteById(...)
public interface CursoRepository extends JpaRepository<Curso, Long> {
}
