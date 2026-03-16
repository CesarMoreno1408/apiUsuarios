package com.usta.apiusuarios.service;

import com.usta.apiusuarios.model.Usuario;
import com.usta.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("El email no tiene un formato valido");
        }
        if (password == null || password.isBlank()) {
            throw new RuntimeException("La password es obligatoria");
        }
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya esta registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
