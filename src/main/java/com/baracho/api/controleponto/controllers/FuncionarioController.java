package com.baracho.api.controleponto.controllers;

import com.baracho.api.controleponto.dto.FuncionarioDto;
import com.baracho.api.controleponto.entities.Funcionario;
import com.baracho.api.controleponto.response.Response;
import com.baracho.api.controleponto.service.FuncionarioService;
import com.baracho.api.controleponto.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping(value ="api/funcionarios")
public class FuncionarioController {

    private static final Logger log = LoggerFactory.getLogger(FuncionarioController.class);

    @Autowired
    private FuncionarioService funcionarioService;

    public FuncionarioController() {
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<FuncionarioDto>> atualizar(@PathVariable("id") Long id, @Valid @RequestBody FuncionarioDto funcionarioDto, BindingResult result) throws NoSuchAlgorithmException{

        log.info("Atualizando funcionario: {}", funcionarioDto.toString());
        Response<FuncionarioDto> response = new Response<FuncionarioDto>();
        Optional<Funcionario> funcionario = funcionarioService.buscarPorId(id);

        if(!funcionario.isPresent()) {
            result.addError(new ObjectError("funcionario", "Funcionario não encontrado"));
        }

        this.atualizarDadosFuncionario(funcionario.get(), funcionarioDto, result);

        if(result.hasErrors()){
            log.error("Erro validando funcionario: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        this.funcionarioService.persistir(funcionario.get());
        response.setData(this.converterFuncionarioEmFuncionarioDto(funcionario.get()));

        return ResponseEntity.ok(response);
    }

    /**
     * Retorna um Dto com os dados de um funcionario
     * @param funcionario
     * @return FuncionarioDto
     */
    private FuncionarioDto converterFuncionarioEmFuncionarioDto(Funcionario funcionario) {

        FuncionarioDto funcionarioDto = new FuncionarioDto();
        funcionarioDto.setId(funcionario.getId());
        funcionarioDto.setNome(funcionario.getNome());
        funcionarioDto.setEmail(funcionario.getEmail());
        funcionario.getQtdHorasAlmocoOptional().ifPresent(qtdHorasAlmocoOptional -> funcionarioDto.setQtHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmocoOptional))));
        funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(qtdHorasTrabalhoDiaOpt -> funcionarioDto.setQtHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabalhoDiaOpt))));
        funcionario.getValorHoraOpt().ifPresent(valorHoraOpt -> funcionarioDto.setValorHora(Optional.of(valorHoraOpt.toString())));
        return funcionarioDto;
    }

    /**
     * Atualiza os dados do funcionario com base nos dados encontrados no Dto.
     * @param funcionario
     * @param funcionarioDto
     * @param result
     */
    private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioDto funcionarioDto, BindingResult result) throws NoSuchAlgorithmException{
        funcionario.setNome(funcionarioDto.getNome());

        if(!funcionario.getEmail().equals(funcionarioDto.getEmail())){
            this.funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
                    .ifPresent(fun -> result.addError(new ObjectError("email", "email já existente")));

            funcionario.setEmail(funcionarioDto.getEmail());
        }

        funcionario.setQtdHorasAlmoco(null);
        funcionarioDto.getQtHorasAlmoco()
                .ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

        funcionario.setQtdHorasTrabalhoDia(null);
        funcionarioDto.getQtHorasTrabalhoDia()
                .ifPresent(qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));

        funcionario.setValorHora(null);
        funcionarioDto.getValorHora()
                .ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

        if(funcionarioDto.getSenha().isPresent()) {
            funcionario.setSenha(PasswordUtils.gerarBcrypt(funcionarioDto.getSenha().get()));
        }
    }

}
