package com.controle.controleme.mock;

import com.controle.controleme.model.entity.Lancamento;
import com.controle.controleme.model.enuns.StatusLancamento;
import com.controle.controleme.model.enuns.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LancamentoMock {
    public static Lancamento criarLancamento(){
        return Lancamento.builder()
                .ano(2021)
                .mes(1)
                .descricao("lancamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }
    public static Lancamento criarLancamentoComId(Long id){
        return Lancamento.builder()
                .id(id)
                .ano(2021)
                .mes(1)
                .descricao("lancamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }
}
