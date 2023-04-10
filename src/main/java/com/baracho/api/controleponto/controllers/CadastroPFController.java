package com.baracho.api.controleponto.controllers;

import com.baracho.api.controleponto.dto.CadastroPFDto;
import com.baracho.api.controleponto.entities.Empresa;
import com.baracho.api.controleponto.entities.Funcionario;
import com.baracho.api.controleponto.enums.PerfilEnum;
import com.baracho.api.controleponto.response.Response;
import com.baracho.api.controleponto.service.EmpresaServices;
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
@RequestMapping("/api/cadatrar-pf")
@CrossOrigin("*")
public class CadastroPFController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPFController.class);

    @Autowired
    EmpresaServices empresaServices;

    @Autowired
    FuncionarioService funcionarioService;

    /**
     *
     * @param cadastroPFDto
     * @param result
     * @return ResponseEntity<Response<CadastroPFDto>>
     * @throws NoSuchAlgorithmException
     */
    @PostMapping
    public ResponseEntity<Response<CadastroPFDto>> cadastroPFDto(@Valid @RequestBody CadastroPFDto cadastroPFDto, BindingResult result)  throws NoSuchAlgorithmException {
        log.info("Cadastrando Pessoa Fisica: {} ", cadastroPFDto.toString());
        Response<CadastroPFDto> response = new Response<CadastroPFDto>();
        validarDadosExistentes(cadastroPFDto, result);

        Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPFDto, result);

        if( result.hasErrors()){
            log.error("Erro validando dados de cadastro de Pessoa Fisica: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Empresa> empresa = this.empresaServices.buscarPorCnpj(cadastroPFDto.getCnpj());
        empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
        this.funcionarioService.persistir(funcionario);

        response.setData(this.conveterCadastroPFDto(funcionario));
        return ResponseEntity.ok(response);


    }


    private void validarDadosExistentes(CadastroPFDto cadastroPFDto, BindingResult result) {
        Optional<Empresa> empresa = this.empresaServices.buscarPorCnpj(cadastroPFDto.getCnpj());

        if (!empresa.isPresent()) {
            result.addError(new ObjectError("empresa", "Empresa não encontrada"));
        }

        this.funcionarioService.buscarPorCpf(cadastroPFDto.getCpf())
                .ifPresent(fun -> result.addError(new ObjectError("funcionario", "CPF já cadastrado")));

        this.funcionarioService.buscarPorEmail(cadastroPFDto.getEmail())
                .ifPresent(fun -> result.addError(new ObjectError("funcionario", "Email já existente")));
    }

    /**
     * Converte DTo para funcionario
     * @param cadastroPFDto
     * @param result
     * @return Funcionario
     * @throws NoSuchAlgorithmException
     */
    private Funcionario converterDtoParaFuncionario(CadastroPFDto cadastroPFDto, BindingResult result) throws NoSuchAlgorithmException{
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(cadastroPFDto.getNome());
        funcionario.setEmail(cadastroPFDto.getEmail());
        funcionario.setCpf(cadastroPFDto.getCpf());
        funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
        funcionario.setSenha(PasswordUtils.gerarBcrypt(cadastroPFDto.getSenha()));
        cadastroPFDto.getQtdHorasAlmoco()
                .ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
        cadastroPFDto.getQtdHorasTrabalhoDia()
                .ifPresent(qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));

        cadastroPFDto.getValorHora()
                .ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

        return funcionario;
    }

    private CadastroPFDto conveterCadastroPFDto(Funcionario funcionario) {
        CadastroPFDto cadastroPFDto = new CadastroPFDto();
        cadastroPFDto.setId(funcionario.getId());
        cadastroPFDto.setNome(funcionario.getNome());
        cadastroPFDto.setEmail(funcionario.getEmail());
        cadastroPFDto.setCpf(funcionario.getCpf());
        cadastroPFDto.setCnpj(funcionario.getEmpresa().getCnpj());
        funcionario.getQtdHorasAlmocoOptional()
                .ifPresent(qtHorasAlmoco -> cadastroPFDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtHorasAlmoco))));

        funcionario.getQtdHorasTrabalhoDiaOpt()
                .ifPresent(qtHorasTrabalhoDia -> cadastroPFDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtHorasTrabalhoDia))));

        funcionario.getValorHoraOpt()
                .ifPresent(valorHora -> cadastroPFDto.setValorHora(Optional.of(valorHora.toString())));

        return cadastroPFDto;
    }


}
