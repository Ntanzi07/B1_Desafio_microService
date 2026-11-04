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

// ---------------- Ranking (outro servico) ----------------
// O ranking NAO vem do Core. A pagina foi servida pelo Core (localhost:8080),
// mas esta chamada vai para outro processo (localhost:8081).
// Por serem origens diferentes, o navegador so aceita a resposta
// se o Ranking Service liberar CORS para http://localhost:8080.
const RANKING_API = "http://localhost:8081";

async function carregarRanking() {
    const lista = document.getElementById("ranking");
    lista.innerHTML = "";
    try {
        // fetch simples, SEM o header Authorization do Core:
        // o GET do ranking e publico, e a credencial admin/123456 nao vale la.
        const response = await fetch(`${RANKING_API}/api/ranking`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const ranking = await response.json();

        if (ranking.length === 0) {
            lista.innerHTML = "<li>Ranking vazio (o Core ainda nao enviou dados).</li>";
            return;
        }
        // textContent em vez de innerHTML: o nome vem do usuario e poderia conter HTML.
        ranking.forEach(r => {
            const li = document.createElement("li");
            li.textContent = `${r.nome} | ${r.pontos} pts (cursos: ${r.cursosConcluidos}, moedas: ${r.moedas})`;
            lista.appendChild(li);
        });
    } catch (e) {
        // Se cair aqui com "Failed to fetch", veja o console (F12):
        // ou o Ranking esta fora do ar, ou o CORS bloqueou.
        lista.innerHTML = `<li>Ranking Service indisponivel: ${e.message}</li>`;
    }
}
