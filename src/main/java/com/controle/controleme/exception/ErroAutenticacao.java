package com.controle.controleme.exception;

public class ErroAutenticacao extends RuntimeException{
    public ErroAutenticacao(String mensagem){
        super(mensagem);
    }
}
