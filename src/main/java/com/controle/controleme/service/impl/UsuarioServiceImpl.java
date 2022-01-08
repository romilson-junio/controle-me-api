package com.controle.controleme.service.impl;

import com.controle.controleme.exception.ErroAutenticacao;
import com.controle.controleme.exception.RegraNegocioException;
import com.controle.controleme.model.entity.Usuario;
import com.controle.controleme.model.enuns.EnumMessagesErrors;
import com.controle.controleme.model.repository.UsuarioRepository;
import com.controle.controleme.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;
    private PasswordEncoder encoder;

    public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);
        if(!usuario.isPresent()){
            throw new ErroAutenticacao(EnumMessagesErrors.USUARIO_NAO_ENCONTRADO_PARA_O_EMAIL_INFORMADO.getDescricao());
        }
        /**
         * Verifica se a senha informada "senha"
         * é a mesma do usuario.get().getSenha()
         */
        if(!encoder.matches(senha, usuario.get().getSenha())){
            throw new ErroAutenticacao(EnumMessagesErrors.SENHA_INVALIDA.getDescricao());
        }

        return usuario.get();
    }

    @Override
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        criptografarSenha(usuario);
        return repository.save(usuario);
    }

    private void criptografarSenha(Usuario usuario){
        String senha = usuario.getSenha();
        String senhaCripto = encoder.encode(senha);
        usuario.setSenha(senhaCripto);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if (existe) throw new RegraNegocioException(EnumMessagesErrors.JA_EXISTE_UM_USUARIO_COM_ESTE_EMAIL.getDescricao());
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return repository.findById(id);
    }
}
