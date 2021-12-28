package com.controle.controleme.api.resource;

import com.controle.controleme.api.dto.AtualizaStatusDTO;
import com.controle.controleme.api.dto.LancamentoDTO;
import com.controle.controleme.exception.RegraNegocioException;
import com.controle.controleme.model.entity.Lancamento;
import com.controle.controleme.model.entity.Usuario;
import com.controle.controleme.model.enuns.EnumMessagesErrorResponse;
import com.controle.controleme.model.enuns.StatusLancamento;
import com.controle.controleme.model.enuns.TipoLancamento;
import com.controle.controleme.service.LancamentoService;
import com.controle.controleme.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity save(@RequestBody LancamentoDTO dto){
        try{
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        } catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody LancamentoDTO dto){
        return service.obterPorId(id).map( entity -> {
            try{
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException e){
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }).orElseGet(
            () -> new ResponseEntity(
                    EnumMessagesErrorResponse.LANCAMENTO_NAO_ENCONTRADO_NA_BASE_DE_DADOS.getDescricao(),
                    HttpStatus.BAD_REQUEST)
        );
    }

    @PutMapping("{id}/atualizar-status")
    public ResponseEntity updateStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto){

        return service.obterPorId(id).map( entity -> {
            try{
                StatusLancamento statusLancamento = StatusLancamento.valueOf(dto.getStatus());
                if(Objects.isNull(statusLancamento)){
                    return ResponseEntity.badRequest().body(
                            EnumMessagesErrorResponse.NAO_FOI_POSSIVEL_ATUALIZAR_O_STATUS_DO_LANCAMENTO.getDescricao());
                }
                entity.setStatus(statusLancamento);
                entity = service.atualizar(entity);
                return ResponseEntity.ok(entity);
            } catch (RegraNegocioException e){
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }).orElseGet(
                () -> new ResponseEntity(
                        EnumMessagesErrorResponse.LANCAMENTO_NAO_ENCONTRADO_NA_BASE_DE_DADOS.getDescricao(),
                        HttpStatus.BAD_REQUEST)
        );

    }

    @DeleteMapping("{id}")
    public ResponseEntity delete( @PathVariable("id") Long id){
        return service.obterPorId(id).map( entidade -> {
            service.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(
            () -> new ResponseEntity(
                    EnumMessagesErrorResponse.LANCAMENTO_NAO_ENCONTRADO_NA_BASE_DE_DADOS.getDescricao(),
                    HttpStatus.BAD_REQUEST)
        );
    }



    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario
        ){
        Lancamento filtro = new Lancamento();
        filtro.setMes(mes);
        filtro.setAno(ano);
        filtro.setDescricao(descricao);
        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if(!usuario.isPresent()){
            return ResponseEntity.badRequest().body(
                    EnumMessagesErrorResponse.NAO_FOI_POSSIVEL_REALIZAR_A_CONSULTA_USUARIO_NAO_ENCONTRADO.getDescricao());
        }
        filtro.setUsuario(usuario.get());
        List<Lancamento> lancamentos = service.buscar(filtro);
        return ResponseEntity.ok(lancamentos);
    }

    @GetMapping("{id}")
    public ResponseEntity obterLancamento(@PathVariable("id") Long id){
        return service.obterPorId(id)
                .map( lancamento -> new ResponseEntity(converter(lancamento),HttpStatus.OK))
                .orElseGet( () -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    private LancamentoDTO converter(Lancamento lancamento){
        return LancamentoDTO.builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .mes(lancamento.getMes())
                .ano(lancamento.getAno())
                .status(lancamento.getStatus().name())
                .tipo(lancamento.getTipo().name())
                .usuario(lancamento.getUsuario().getId()).build();
    }

    private Lancamento converter(LancamentoDTO dto){
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());
        Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException(EnumMessagesErrorResponse.USUARIO_NAO_ENCONTRADO_NA_BASE_DE_DADOS.getDescricao()));
        lancamento.setUsuario(usuario);
        if(Objects.nonNull(dto.getTipo())){
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }
        if(Objects.nonNull(dto.getStatus())){
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }

        return lancamento;
    }
}
