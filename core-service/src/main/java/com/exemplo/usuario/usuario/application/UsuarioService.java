package com.exemplo.usuario.usuario.application;

import com.exemplo.usuario.usuario.domain.Assinatura;
import com.exemplo.usuario.usuario.domain.Usuario;
import com.exemplo.usuario.usuario.api.UsuarioRequestDTO;
import com.exemplo.usuario.usuario.api.UsuarioResponseDTO;
import com.exemplo.usuario.usuario.internal.persistence.UsuarioRepository;
import com.exemplo.usuario.integracao.ranking.RankingDadosAlteradosEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Camada: SERVICE.
// Aqui ficam as regras de negocio do caso de uso de usuario.
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventos;

    // Tres dependencias sao injetadas aqui:
    // 1) UsuarioRepository -> bean criado pelo Spring Data
    // 2) PasswordEncoder -> bean declarado em SecurityConfig
    // 3) ApplicationEventPublisher -> do proprio Spring, para avisar o Ranking (via RankingSyncListener)
    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder,
                          ApplicationEventPublisher eventos) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.eventos = eventos;
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        // A camada service costuma preparar/normalizar dados para a regra de negocio.
        String emailNormalizado = dto.getEmail() == null ? null : dto.getEmail().trim().toLowerCase();

        // Valida regra de unicidade antes de persistir.
        if (repository.existsByEmailValor(emailNormalizado)) {
            throw new RuntimeException("E-mail ja cadastrado");
        }

        // A senha e criptografada na camada de service.
        // Isso e importante didaticamente: o controller nao deve criptografar,
        // e o repository nao deve conter regra de negocio.
        Usuario usuario = new Usuario(
                dto.getNome(),
                emailNormalizado,
                passwordEncoder.encode(dto.getSenha())
        );

        // Cria a assinatura padrao e vincula ao usuario.
        Assinatura assinatura = new Assinatura(usuario);
        usuario.vincularAssinatura(assinatura);

        Usuario salvo = repository.save(usuario);

        // Publica o evento; o envio de verdade so acontece apos o commit (AFTER_COMMIT).
        // Assim o usuario novo ja aparece no ranking com 0 pontos, como no monolito.
        eventos.publishEvent(new RankingDadosAlteradosEvent(
                salvo.getId(), salvo.getNome(),
                assinatura.getCursosConcluidosComSucesso(), assinatura.getMoedas()));

        return toDTO(salvo);
    }

    private UsuarioResponseDTO toDTO(Usuario usuario) {
        var assinatura = usuario.getAssinatura();
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                assinatura != null ? assinatura.getPlano().name() : null,
                assinatura != null ? assinatura.getCreditosCursos() : null,
                assinatura != null ? assinatura.getCursosConcluidosComSucesso() : null,
                assinatura != null ? assinatura.getMoedas() : null
        );
    }
}
