package com.controle.controleme.model.enuns;

public enum EnumMessagesErrors {
    INFORME_UMA_DESCRICAO_VALIDA(1L,"Informe uma descrição válida!"),
    INFORME_UM_MES_VALIDO(2L, "Informe um Mês válido!"),
    INFORME_UM_ANO_VALIDO(3L,"Informe um Ano válido!"),
    INFORME_UM_USUARIO(4L,"Informe um Usuário!"),
    INFORME_UM_VALOR_VALIDO(5L,"Informe um Valor válido!"),
    INFORME_UM_TIPO_DE_LANCAMENTO(6L,"Informe um tipo de lançamento!"),
    USUARIO_NAO_ENCONTRADO_PARA_O_EMAIL_INFORMADO(7L,"Usuário não encontrado para o e-mail informado!"),
    SENHA_INVALIDA(8L,"Senha inválida!"),
    JA_EXISTE_UM_USUARIO_COM_ESTE_EMAIL(9L,"Já existe um usuário com este e-mail!");

    private final String descricao;
    private final Long codigo;

    public String getDescricao() {
        return descricao;
    }

    public Long getCodigo() {
        return codigo;
    }

    EnumMessagesErrors(Long codigo, String message) {
        this.codigo = codigo;
        this.descricao = message;
    }
}
