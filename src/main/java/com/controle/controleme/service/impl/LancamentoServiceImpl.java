package com.controle.controleme.service.impl;

import com.controle.controleme.exception.RegraNegocioException;
import com.controle.controleme.model.entity.Lancamento;
import com.controle.controleme.model.enuns.EnumMessagesErrors;
import com.controle.controleme.model.enuns.StatusLancamento;
import com.controle.controleme.model.enuns.TipoLancamento;
import com.controle.controleme.model.repository.LancamentoRepository;
import com.controle.controleme.service.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private LancamentoRepository repository;

    public LancamentoServiceImpl(LancamentoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento) {
        lancamento.setStatus(statusLancamento);
        atualizar(lancamento);
    }

    @Override
    public void validar(Lancamento lancamento) {
        if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")){
            throw new RegraNegocioException(EnumMessagesErrors.INFORME_UMA_DESCRICAO_VALIDA.getDescricao());
        }
        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12){
            throw new RegraNegocioException(EnumMessagesErrors.INFORME_UM_MES_VALIDO.getDescricao());
        }
        if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4){
            throw new RegraNegocioException(EnumMessagesErrors.INFORME_UM_ANO_VALIDO.getDescricao());
        }
        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
            throw new RegraNegocioException(EnumMessagesErrors.INFORME_UM_USUARIO.getDescricao());
        }
        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ONE) < 1){
            throw new RegraNegocioException(EnumMessagesErrors.INFORME_UM_VALOR_VALIDO.getDescricao());
        }
        if(lancamento.getTipo() == null){
            throw new RegraNegocioException(EnumMessagesErrors.INFORME_UM_TIPO_DE_LANCAMENTO.getDescricao());
        }
    }

    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(Long id) {
        BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO);
        BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);
        if(Objects.isNull(receitas)){
            receitas = BigDecimal.ZERO;
        }
        if(Objects.isNull(despesas)){
            despesas = BigDecimal.ZERO;
        }
        return receitas.subtract(despesas);
    }
}
