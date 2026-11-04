package com.exemplo.usuario.shared.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Diferenca didatica importante:
// - @RestController costuma devolver JSON.
// - @Controller costuma devolver nome de view/template.
@Controller
public class HomeController {

    // @GetMapping mapeia a rota HTTP GET /home.
    @GetMapping("/home")
    public String home() {
        // Como esta classe usa @Controller, retornar "home"
        // significa: renderize o template home.html.
        return "home";
    }
}
