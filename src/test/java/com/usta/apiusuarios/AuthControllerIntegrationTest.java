package com.usta.apiusuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usta.apiusuarios.model.Usuario;
import com.usta.apiusuarios.repository.UsuarioRepository;
import com.usta.apiusuarios.service.UsuarioService;
import com.usta.apiusuarios.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/auth";
    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final String DUPLICATE_EMAIL = "duplicate@example.com";

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setEmail(DUPLICATE_EMAIL);
        usuario.setPassword(passwordEncoder.encode(VALID_PASSWORD));
        usuarioRepository.save(usuario);
    }

    @Test
    public void testRegister_ValidInput_ShouldReturn201() throws Exception {
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", VALID_EMAIL)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Usuario registrado exitosamente")));

        Usuario usuarioCreado = usuarioRepository.findByEmail(VALID_EMAIL).orElse(null);
        assert usuarioCreado != null;
        assert usuarioCreado.getEmail().equals(VALID_EMAIL);
        assert passwordEncoder.matches(VALID_PASSWORD, usuarioCreado.getPassword());
    }

    @Test
    public void testRegister_DuplicateEmail_ShouldReturn400() throws Exception {
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", DUPLICATE_EMAIL)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("El email ya esta registrado")));
    }

    @Test
    public void testRegister_InvalidEmailFormat_ShouldReturn400() throws Exception {
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", INVALID_EMAIL)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("El email no tiene un formato valido")));
    }

    @Test
    public void testRegister_EmptyEmail_ShouldReturn400() throws Exception {
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "")
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_EmptyPassword_ShouldReturn400() throws Exception {
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", VALID_EMAIL)
                        .param("password", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogin_ValidCredentials_ShouldReturn200WithToken() throws Exception {
        usuarioService.registrarUsuario(VALID_EMAIL, VALID_PASSWORD);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", VALID_EMAIL)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.token", isA(String.class)))
                .andExpect(jsonPath("$.token", not(emptyString())));

        String responseContent = mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", VALID_EMAIL)
                        .param("password", VALID_PASSWORD))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(responseContent).get("token").asText();
        String extractedEmail = jwtUtil.extractUsername(token);

        assert extractedEmail.equals(VALID_EMAIL);
        assert jwtUtil.validateToken(token, VALID_EMAIL);
    }

    @Test
    public void testLogin_InvalidEmail_ShouldReturn401() throws Exception {
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "nonexistent@example.com")
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Credenciales invalidas")));
    }

    @Test
    public void testLogin_InvalidPassword_ShouldReturn401() throws Exception {
        usuarioService.registrarUsuario(VALID_EMAIL, VALID_PASSWORD);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", VALID_EMAIL)
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Credenciales invalidas")));
    }

    @Test
    public void testLogin_EmptyEmail_ShouldReturn401() throws Exception {
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "")
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_EmptyPassword_ShouldReturn401() throws Exception {
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", VALID_EMAIL)
                        .param("password", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRegister_ResponseContract_ShouldMatchSchema() throws Exception {
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", VALID_EMAIL)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(Map.class)))
                .andExpect(jsonPath("$.message", isA(String.class)))
                .andExpect(jsonPath("$.message", not(emptyString())));
    }

    @Test
    public void testLogin_ResponseContract_ShouldMatchSchema() throws Exception {
        usuarioService.registrarUsuario(VALID_EMAIL, VALID_PASSWORD);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", VALID_EMAIL)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(Map.class)))
                .andExpect(jsonPath("$.token", isA(String.class)))
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    public void testErrorResponseContract_ShouldMatchSchema() throws Exception {
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", DUPLICATE_EMAIL)
                        .param("password", VALID_PASSWORD))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", isA(Map.class)))
                .andExpect(jsonPath("$.error", isA(String.class)))
                .andExpect(jsonPath("$.error", not(emptyString())));
    }
}
