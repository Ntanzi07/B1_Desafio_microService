package com.exemplo.usuario.usuario.internal.persistence;

import com.exemplo.usuario.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository de Usuario.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Como EmailUsuario e um Value Object embutido, o Spring Data consegue navegar ate email.valor.
    boolean existsByEmailValor(String email);
}
