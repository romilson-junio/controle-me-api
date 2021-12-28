package com.controle.controleme.service.impl;

import com.controle.controleme.exception.ErroAutenticacao;
import com.controle.controleme.exception.RegraNegocioException;
import com.controle.controleme.model.entity.Usuario;
import com.controle.controleme.model.enuns.EnumMessagesErrors;
import com.controle.controleme.model.repository.UsuarioRepository;
import com.controle.controleme.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);
        if(!usuario.isPresent()){
            throw new ErroAutenticacao(EnumMessagesErrors.USUARIO_NAO_ENCONTRADO_PARA_O_EMAIL_INFORMADO.getDescricao());
        }
        if(!usuario.get().getSenha().equals(senha)){
            throw new ErroAutenticacao(EnumMessagesErrors.SENHA_INVALIDA.getDescricao());
        }

        return usuario.get();
    }

    @Override
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
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
