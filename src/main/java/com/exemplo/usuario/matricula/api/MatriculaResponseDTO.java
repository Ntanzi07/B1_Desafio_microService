package com.exemplo.usuario.matricula.api;

// DTO de saida para devolver os dados da matricula ao cliente.
public class MatriculaResponseDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private Long cursoId;
    private String cursoTitulo;
    private String status;
    private Double notaFinal;
    private boolean bonus;

    public MatriculaResponseDTO() {
    }

    public MatriculaResponseDTO(Long id, Long usuarioId, String usuarioNome, Long cursoId, String cursoTitulo,
                                String status, Double notaFinal, boolean bonus) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.cursoId = cursoId;
        this.cursoTitulo = cursoTitulo;
        this.status = status;
        this.notaFinal = notaFinal;
        this.bonus = bonus;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public String getCursoTitulo() {
        return cursoTitulo;
    }

    public String getStatus() {
        return status;
    }

    public Double getNotaFinal() {
        return notaFinal;
    }

    public boolean isBonus() {
        return bonus;
    }
}
