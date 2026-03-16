package com.usta.apiusuarios.controller;

import com.usta.apiusuarios.service.CustomUserDetailsService;
import com.usta.apiusuarios.service.UsuarioService;
import com.usta.apiusuarios.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Endpoints para autenticacion de usuarios")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario con email y password")
    public ResponseEntity<?> register(@RequestParam("email") String email,
                                      @RequestParam("password") String password) {
        try {
            usuarioService.registrarUsuario(email, password);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Autentica al usuario y devuelve un token JWT")
    public ResponseEntity<?> login(@RequestParam("email") String email,
                                   @RequestParam("password") String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String token = jwtUtil.generateToken(userDetails.getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Credenciales invalidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
