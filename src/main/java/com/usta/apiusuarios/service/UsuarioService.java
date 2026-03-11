package com.usta.apiusuarios.service;

import com.usta.apiusuarios.model.Usuario;
import com.usta.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(String email, String password) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
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

