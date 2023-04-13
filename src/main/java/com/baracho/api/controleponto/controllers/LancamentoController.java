package com.baracho.api.controleponto.controllers;

import com.baracho.api.controleponto.dto.LancamentoDto;
import com.baracho.api.controleponto.entities.Funcionario;
import com.baracho.api.controleponto.entities.Lancamento;
import com.baracho.api.controleponto.enums.TipoEnum;
import com.baracho.api.controleponto.response.Response;
import com.baracho.api.controleponto.service.FuncionarioService;
import com.baracho.api.controleponto.service.LancamentoService;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
       * Adiciona um novo lancamento
       * @param lancamentoDto
       * @param result
       * @return ResponseEntity<Response<LancamentoDto>>
       * @throws ParseException
       */
      @PostMapping
      public ResponseEntity<Response<LancamentoDto>> adicionar(@Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
            log.info("Adicionando um lancamento: {}", lancamentoDto.toString());
            Response<LancamentoDto> response = new Response<LancamentoDto>();
            validarFuncionario(lancamentoDto, result);
            Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);
            if (result.hasErrors()){
                  log.error("Erro validando lancamentos: {}", result.getAllErrors());
                  result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                  return ResponseEntity.badRequest().body(response);
            }

            lancamento = this.lancamentoService.persistir(lancamento);
            response.setData(this.converterLancamentoParaLancamentoDto(lancamento));
            return ResponseEntity.ok(response);
      }

      /**
       * Atualiza um lancamento pelo seu respectivo lancamento
       * @param id
       * @param lancamentoDto
       * @param result
       * @return ResponseEntity<Response<LancamentoDto>>
       * @throws ParseException
       */
      @PutMapping(value = "/{id}")
      public ResponseEntity<Response<LancamentoDto>> atualizar(@PathVariable("id") Long id, @Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
            log.debug("atualizar lancamento: {}", lancamentoDto.toString());
            Response<LancamentoDto> response = new Response<LancamentoDto>();
            validarFuncionario(lancamentoDto, result);
            lancamentoDto.setId(Optional.of(id));
            Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);

            if (result.hasErrors()){
                  log.error("Erro validando lancamento: {}", result.getAllErrors());
                  result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                  return ResponseEntity.badRequest().body(response);
            }

            lancamento = this.lancamentoService.persistir(lancamento);
            response.setData(this.converterLancamentoParaLancamentoDto(lancamento));
            return ResponseEntity.ok(response);
      }

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
       * Retorna um lancamento por ID.
       * @param id
       * @return ResponseEntity<Response<LancamentoDto>>
       */
      @GetMapping(value = "/{id}")
      public ResponseEntity<Response<LancamentoDto>> listarPorId(@PathVariable("id") Long id){
            log.info("Buscando lancamento por id: {}", id);
            Response<LancamentoDto> response = new Response<LancamentoDto>();
            Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);

            if (!lancamento.isPresent()){
                  log.info("Lancamento não encontrado para o id: {}", id);
                  response.getErrors().add("lancamento não encontrado para o id: " + id);
                  return ResponseEntity.badRequest().body(response);
            }

            response.setData(this.converterLancamentoParaLancamentoDto(lancamento.get()));
            return ResponseEntity.ok(response);
      }

      /**
       * Busca todos os lancamentos
       * @return ResponseEntity<List<LancamentoDto>>
       */
      @GetMapping(value = "/todos")
      public ResponseEntity<List<LancamentoDto>> listarTodos() {
            log.info("Buscando todos os lancamentos");
            List<Lancamento> lancamentos = this.lancamentoService.buscarTodos();
            List<LancamentoDto> lancamentoDto = new ArrayList<LancamentoDto>();
            lancamentos.forEach(lancamento -> lancamentoDto.add(this.converterLancamentoParaLancamentoDto(lancamento)));
            return ResponseEntity.ok(lancamentoDto);
      }

      //	http://localhost:8080/lancamentos/por-data?inicio=2023-01-01&fim=2023-03-31
      /**
       * Retorna lancamento por data
       * @param dataInicio
       * @param dataFim
       * @return ResponseEntity<List<LancamentoDto>>
       */
      @GetMapping(value = "/por-data")
      public ResponseEntity<List<LancamentoDto>> listarPorData(@RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataInicio, @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
            log.info("Buscando lancamento por data");
            List<Lancamento> lancamentos = this.lancamentoService.buscarPorData(dataInicio, dataFim);
            List<LancamentoDto> lancamentosDto = new ArrayList<>();
            lancamentos.forEach(lancamento -> lancamentosDto.add(this.converterLancamentoParaLancamentoDto(lancamento)));
            return ResponseEntity.ok(lancamentosDto);
      }

      /**
       * Deleta um lancamento pelo seu respectivo id
       * @param id
       * @return ResponseEntity<Response<String>>
       */
      @DeleteMapping(value = "/{id}")
      public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id) {
            log.info("Removendo lancamento pelo id: {}", id);
            Response<String> response = new Response<String>();
            Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
            if (!lancamento.isPresent()) {
                 log.error("Erro ao remover lancamento com o id: {}", id);
                 response.getErrors().add("Erro ao remover lancamento. Registro não encontrado para o id " + id);
                 return ResponseEntity.badRequest().body(response);
            }

            this.lancamentoService.remover(id);
            return ResponseEntity.ok(new Response<String>());
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

      /**
       * Converte um lancamentoDto para uma entidade lancamento
       * @param lancamentoDto
       * @param result
       * @return Lancamento
       * @throws ParseException
       */
      private Lancamento converterDtoParaLancamento(LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
            Lancamento lancamento = new Lancamento();

            if(lancamentoDto.getId().isPresent()){
                  Optional<Lancamento> lanc = this.lancamentoService.buscarPorId(lancamentoDto.getId().get());
                  if(lanc.isPresent()){
                        lancamento = lanc.get();
                  }else {
                        result.addError(new ObjectError("lancamento","Lancçamento não encontrado"));
                  }


            }else{
                  lancamento.setFuncionario(new Funcionario());
                  lancamento.getFuncionario().setId(lancamentoDto.getFuncionarioId());
            }

            lancamento.setDescricao(lancamentoDto.getDescricao());
            lancamento.setLocalizacao(lancamentoDto.getLocalizacao());
            lancamento.setData(this.dateFormat.parse(lancamentoDto.getData()));

            if (EnumUtils.isValidEnum(TipoEnum.class, lancamentoDto.getTipo())) {
                  lancamento.setTipo(TipoEnum.valueOf(lancamentoDto.getTipo()));
            }else{
                  result.addError(new ObjectError("tipo", "Tipo inválido"));
            }

            return lancamento;
      }

}
