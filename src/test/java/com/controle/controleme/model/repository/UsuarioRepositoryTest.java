package com.controle.controleme.model.repository;

import com.controle.controleme.model.entity.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void existesByEmail(){
        //Cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        //Ação
        boolean result = repository.existsByEmail("usuario@email.com.br");
        //Verificação
        Assertions.assertTrue(result);
    }

    @Test
    public void notExistesByEmail(){
        boolean result = repository.existsByEmail("email@email.com.br");
        Assertions.assertFalse(result);
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados(){
        //Cenário
        Usuario usuario = criarUsuario();
        //Ação
        Usuario usuarioSalvo =  repository.save(usuario);
        Assertions.assertNotNull(usuarioSalvo.getId());
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail(){
        //Cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        //Verificação
        Optional<Usuario> result = repository.findByEmail("usuario@email.com.br");
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void deveRetornarVazioAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase(){
        //Verificação
        Optional<Usuario> result = repository.findByEmail("usuario@email.com.br");
        Assertions.assertFalse(result.isPresent());
    }

    public static Usuario criarUsuario(){
        return Usuario.builder()
                .nome("Usuario")
                .email("usuario@email.com.br")
                .senha("senha")
                .build();
    }
}
