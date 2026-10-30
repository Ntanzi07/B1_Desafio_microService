const AUTH = "Basic " + btoa("admin:123456");

async function api(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            "Authorization": AUTH,
            ...(options.headers || {})
        }
    });

    if (response.status === 204) return null;
    const text = await response.text();
    if (!response.ok) throw new Error(text || "Erro na requisição");
    return text ? JSON.parse(text) : null;
}

async function criarUsuario() {
    const payload = {
        nome: document.getElementById("nome").value,
        email: document.getElementById("email").value,
        senha: document.getElementById("senha").value
    };
    await api("/api/usuarios", { method: "POST", body: JSON.stringify(payload) });
    listarUsuarios();
}

async function listarUsuarios() {
    const usuarios = await api("/api/usuarios");
    document.getElementById("usuarios").innerHTML = usuarios.map(u =>
        `<li>${u.id} - ${u.nome} | plano: ${u.plano} | créditos: ${u.creditosCursos} | concluídos: ${u.cursosConcluidosComSucesso} | moedas: ${u.moedas}</li>`
    ).join("");
}

async function criarCurso() {
    const payload = {
        titulo: document.getElementById("tituloCurso").value,
        descricao: document.getElementById("descricaoCurso").value
    };
    await api("/api/cursos", { method: "POST", body: JSON.stringify(payload) });
    listarCursos();
}

async function listarCursos() {
    const cursos = await api("/api/cursos");
    document.getElementById("cursos").innerHTML = cursos.map(c =>
        `<li>${c.id} - ${c.titulo}</li>`
    ).join("");
}

async function matricular() {
    const payload = {
        usuarioId: Number(document.getElementById("matUsuarioId").value),
        cursoId: Number(document.getElementById("matCursoId").value),
        bonus: document.getElementById("matBonus").value === "true"
    };
    const result = await api("/api/matriculas", { method: "POST", body: JSON.stringify(payload) });
    alert(`Matrícula criada: ${result.id}`);
}

async function concluirMatricula() {
    const id = document.getElementById("matriculaId").value;
    const payload = { notaFinal: Number(document.getElementById("notaFinal").value) };
    const result = await api(`/api/matriculas/${id}/concluir`, { method: "PUT", body: JSON.stringify(payload) });
    alert(`Matrícula ${result.id} concluída com status ${result.status}`);
}

async function listarMatriculasUsuario() {
    const usuarioId = document.getElementById("consultaUsuarioId").value;
    const matriculas = await api(`/api/matriculas/usuario/${usuarioId}`);
    document.getElementById("matriculas").innerHTML = matriculas.map(m =>
        `<li>${m.id} - ${m.cursoTitulo} | status: ${m.status} | nota: ${m.notaFinal ?? '-'} | bônus: ${m.bonus}</li>`
    ).join("");
}
