package com.exemplo.usuario.usuario.api;

import com.exemplo.usuario.usuario.api.UsuarioRequestDTO;
import com.exemplo.usuario.usuario.api.UsuarioResponseDTO;
import com.exemplo.usuario.usuario.application.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Camada: CONTROLLER.
// Esta camada fala com o mundo externo via HTTP.
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios")
public class UsuarioRestController {

    private final UsuarioService service;

    // DI por construtor: o Spring injeta um bean de UsuarioService aqui.
    // Isso acontece porque UsuarioService esta anotado com @Service.
    public UsuarioRestController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios")
    public List<UsuarioResponseDTO> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por id")
    public UsuarioResponseDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar usuario com assinatura basica")
    public UsuarioResponseDTO criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return service.criar(dto);
    }
}
