package com.controle.controleme.service;

import com.controle.controleme.exception.ErroAutenticacao;
import com.controle.controleme.exception.RegraNegocioException;
import com.controle.controleme.model.entity.Usuario;
import com.controle.controleme.model.repository.UsuarioRepository;
import com.controle.controleme.service.impl.UsuarioServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.assertj.core.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @Test(expected = Test.None.class)
    public void deveSalvarUsuario(){
        //cenario
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder().id(1L).nome("Usuario").email("email@gmail.com").senha("senha").build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
        //Ação
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
        //Verificação
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("Usuario");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@gmail.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");

    }

    @Test(expected = RegraNegocioException.class)
    public void deveNaoDeveSalvarUmUsuarioComEmailJaCadastrado(){
        String email = "email@gmail.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        service.salvarUsuario(usuario);

        Mockito.verify(repository, Mockito.never()).save(usuario);
    }


    @Test(expected = Test.None.class)
    public void deveAutenticarUmUsuarioComSucesso(){
        //Cenário
        String email = "email@gmail.com";
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("email@gmail.com").senha("senha").build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //Ação
        Usuario result = service.autenticar(email, senha);

        //Verificação
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void deveAutenticarLancarErroQuandoNaoEncontraUsuarioComEmailInformado(){
        //Cenário
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //Ação
        Throwable exception = Assertions.catchThrowable(
                () -> service.autenticar("email@gmail.com", "senha")
        );
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o e-mail informado!");

    }

    @Test
    public void deveAutenticarLancarErroQuandoSenhaForInvalida(){
        //Cenário
        Usuario usuario = Usuario.builder().email("email@gmail.com").senha("senha").build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //Ação

        Throwable exception = Assertions.catchThrowable(
                () -> service.autenticar("email@gmail.com", "senha1")
        );
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida!");


    }

    @Test(expected = Test.None.class)
    public void deveValidarEmail(){
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(Boolean.FALSE);
        //acao
        service.validarEmail("email@gmail.com");
    }

    @Test(expected = RegraNegocioException.class)
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado(){
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(Boolean.TRUE);
        //acao
        service.validarEmail("email@gmail.com");
    }

}
