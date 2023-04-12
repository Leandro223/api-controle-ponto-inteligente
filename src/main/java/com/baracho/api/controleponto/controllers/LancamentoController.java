package com.baracho.api.controleponto.controllers;

import com.baracho.api.controleponto.dto.LancamentoDto;
import com.baracho.api.controleponto.entities.Funcionario;
import com.baracho.api.controleponto.entities.Lancamento;
import com.baracho.api.controleponto.response.Response;
import com.baracho.api.controleponto.service.FuncionarioService;
import com.baracho.api.controleponto.service.LancamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {
      private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
      private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

      @Autowired
      private FuncionarioService funcionarioService;

      @Autowired
      private LancamentoService lancamentoService;

      public LancamentoController() {}

      /**
       * Retorna a listagem de lancamentos de um funcionario
       * @param funcionarioId
       * @param pag
       * @param ord
       * @param dir
       * @return ResponseEntity<Response<Page<LancamentoDto>>>
       */
      @GetMapping(value = "/funcionario/{funcionarioId}")
      public ResponseEntity<Response<Page<LancamentoDto>>> listarPorFuncionarioId(
                @PathVariable("funcionarioId") Long funcionarioId, @RequestParam(value = "pag", defaultValue = "0") int pag,
                @RequestParam(value = "ord", defaultValue = "id") String ord,
                @RequestParam(value = "dir", defaultValue = "DESC") String dir){

            log.info("Buscando lancamentos por Id do funcionario: {}, pagina: {}", funcionarioId, pag);
            Response<Page<LancamentoDto>> response = new Response<Page<LancamentoDto>>();

            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<Lancamento> lancamentos = this.lancamentoService.buscarPorfuncionarioId(funcionarioId, pageRequest);
            Page<LancamentoDto> lancamentoDto = lancamentos
                      .map(lancamento -> this.converterLancamentoParaLancamentoDto(lancamento));

            response.setData(lancamentoDto);

            return ResponseEntity.ok(response);
      }

      /**
       * Converter uma entidade lancamento para seu respectivo Dto
       * @param lancamento
       * @return LancamentoDto
       */
      private LancamentoDto converterLancamentoParaLancamentoDto(Lancamento lancamento) {
            LancamentoDto lancamentoDto = new LancamentoDto();
            lancamentoDto.setId(Optional.of(lancamento.getId()));
            lancamentoDto.setData(this.dateFormat.format(lancamento.getData()));
            lancamentoDto.setTipo(lancamento.getTipo().toString());
            lancamentoDto.setDescricao(lancamento.getDescricao());
            lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
            lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());

            return lancamentoDto;

      }

      /**
       * Valida um funcionario, verificando se ele é existente e valido no sistema
       * @param lancamentoDto
       * @param result
       */
      private void validarFuncionario (LancamentoDto lancamentoDto, BindingResult result){
            if(lancamentoDto.getFuncionarioId() == null){
                  result.addError(new ObjectError("funcionario", "funcionario não informado"));
                  return;
            }
            log.info("Validando funcionario id {}", lancamentoDto.getFuncionarioId());
            Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId());
            if(!funcionario.isPresent()){
                  result.addError(new ObjectError("funcionario", "funcionario não encontrado. ID inexistente."));
            }
      }

}
