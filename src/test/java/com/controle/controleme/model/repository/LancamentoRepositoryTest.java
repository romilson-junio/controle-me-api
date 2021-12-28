package com.controle.controleme.model.repository;

import com.controle.controleme.model.entity.Lancamento;
import com.controle.controleme.model.enuns.StatusLancamento;
import com.controle.controleme.model.enuns.TipoLancamento;

import static com.controle.controleme.mock.LancamentoMock.criarLancamento;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamento = criarLancamento();

        lancamento = repository.save(lancamento);

        assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = this.criarEPersistirUmLancamento();
        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        repository.delete(lancamento);
        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());

        assertThat(lancamentoInexistente).isNull();
    }

    @Test
    public void deveBuscarLancamentoPorId(){
        Lancamento lancamento = this.criarEPersistirUmLancamento();
        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        assertThat(lancamentoEncontrado.isPresent()).isTrue();
    }

    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamento = this.criarEPersistirUmLancamento();
        lancamento.setAno(2020);
        lancamento.setMes(2);
        lancamento.setDescricao("Teste Atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);
        repository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
        assertThat(lancamentoAtualizado.getAno()).isEqualTo(2020);
        assertThat(lancamentoAtualizado.getMes()).isEqualTo(2);
        assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
        assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
    }

    private Lancamento criarEPersistirUmLancamento(){
        Lancamento lancamento = criarLancamento();
        return entityManager.persist(lancamento);
    }



}
