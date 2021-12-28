package com.controle.controleme.service;

import com.controle.controleme.exception.RegraNegocioException;
import com.controle.controleme.mock.LancamentoMock;
import com.controle.controleme.model.entity.Lancamento;
import com.controle.controleme.model.entity.Usuario;
import com.controle.controleme.model.enuns.EnumMessagesErrors;
import com.controle.controleme.model.enuns.StatusLancamento;
import com.controle.controleme.model.repository.LancamentoRepository;
import com.controle.controleme.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarComSucesso(){
        Lancamento lancamentoASalvar = LancamentoMock.criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);
        Lancamento lancamentoSalvo = LancamentoMock.criarLancamentoComId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoASalvar);

        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao(){
        Lancamento lancamentoASalvar = LancamentoMock.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        Assertions.catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class);

        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deveAtualizaLancamento(){
        Lancamento lancamentoSalvo = LancamentoMock.criarLancamentoComId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);
        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        service.atualizar(lancamentoSalvo);

        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    public void deveLancarErroAoTentarAtualizarLancamentoQueAindaNaoFoiSalvo(){
        Lancamento lancamento = LancamentoMock.criarLancamento();

        Assertions.catchThrowableOfType( () -> service.atualizar(lancamento), NullPointerException.class);

        Mockito.verify(repository, Mockito.never()).save(lancamento);
    }

    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = LancamentoMock.criarLancamentoComId(1L);
        service.deletar(lancamento);
        Mockito.verify(repository).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoDeletarUmLancamento(){
        Lancamento lancamento = LancamentoMock.criarLancamento();
        Assertions.catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamentos(){
        Lancamento lancamento = LancamentoMock.criarLancamentoComId(1L);

        List<Lancamento> lista = Arrays.asList(lancamento);

        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> lancamentos = service.buscar(lancamento);
        Assertions.assertThat(lancamentos).isNotEmpty().hasSize(1).contains(lancamento);
    }

    @Test
    public void deveAtualizarStatusLancamento(){
        Lancamento lancamento = LancamentoMock.criarLancamentoComId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;

        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
        service.atualizarStatus(lancamento, novoStatus);

        Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    public void deveObterUmLancamentoPorId(){
        Long id = 1L;
        Lancamento lancamento = LancamentoMock.criarLancamentoComId(1L);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
        Optional<Lancamento> resultado = service.obterPorId(id);
        Assertions.assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioQuandoNaoExiste(){
        Long id = 1L;
        Lancamento lancamento = LancamentoMock.criarLancamentoComId(1L);
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Lancamento> resultado = service.obterPorId(id);
        Assertions.assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveValidarLancamento(){
        Lancamento lancamento = new Lancamento();

        Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UMA_DESCRICAO_VALIDA.getDescricao());

        lancamento.setDescricao("");
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UMA_DESCRICAO_VALIDA.getDescricao());

        lancamento.setDescricao("Salário");
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_MES_VALIDO.getDescricao());

        lancamento.setMes(0);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_MES_VALIDO.getDescricao());

        lancamento.setMes(13);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_MES_VALIDO.getDescricao());

        lancamento.setMes(1);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_ANO_VALIDO.getDescricao());

        lancamento.setAno(202);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_ANO_VALIDO.getDescricao());

        lancamento.setAno(20211);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_ANO_VALIDO.getDescricao());

        lancamento.setAno(2021);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_USUARIO.getDescricao());

        lancamento.setUsuario(new Usuario());
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_USUARIO.getDescricao());

        lancamento.getUsuario().setId(1L);

        lancamento.setUsuario(lancamento.getUsuario());
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_VALOR_VALIDO.getDescricao());

        lancamento.setValor(BigDecimal.ZERO);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_VALOR_VALIDO.getDescricao());

        lancamento.setValor(BigDecimal.valueOf(10));
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage(EnumMessagesErrors.INFORME_UM_TIPO_DE_LANCAMENTO.getDescricao());

    }
}
