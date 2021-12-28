package com.controle.controleme.model.enuns;

public enum EnumMessagesErrorResponse {
    NAO_FOI_POSSIVEL_ATUALIZAR_O_STATUS_DO_LANCAMENTO(
            1L,"Não foi possível atualizar o status do lançamento, envie um status válido!"),
    LANCAMENTO_NAO_ENCONTRADO_NA_BASE_DE_DADOS(
            2L,"Lançamento não encontrado na base de dados."),
    NAO_FOI_POSSIVEL_REALIZAR_A_CONSULTA_USUARIO_NAO_ENCONTRADO(
            3L,"Não foi possível realizar a consulta. Usuário não encontrado na base de dados!"),
    USUARIO_NAO_ENCONTRADO_NA_BASE_DE_DADOS(4L,"Usuário não encontrado na base de dados!");

    private final String descricao;
    private final Long codigo;

    public String getDescricao() {
        return descricao;
    }

    public Long getCodigo() {
        return codigo;
    }

    EnumMessagesErrorResponse(Long codigo, String message) {
        this.codigo = codigo;
        this.descricao = message;
    }
}
