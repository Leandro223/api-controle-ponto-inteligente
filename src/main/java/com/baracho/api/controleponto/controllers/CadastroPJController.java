package com.baracho.api.controleponto.controllers;

import com.baracho.api.controleponto.dto.CadastroPJDto;
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
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPJController {

    private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private EmpresaServices empresaServices;

    public CadastroPJController(){}

    /**
     * Cadastra uma pessoa juridica no sistema
     * @param cadastroPJDto
     * @param result
     * @return ResponseEntity<Response<CadastroPJDto>>
     * @throws NoSuchAlgorithmException
     */
    @PostMapping
    public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto, BindingResult result) throws NoSuchAlgorithmException{
        log.info("cadastrar PJ: {}", cadastroPJDto.toString());

        Response<CadastroPJDto> response = new Response<CadastroPJDto>();
        validarDadosExistentes(cadastroPJDto, result);
        Empresa empresa = this.converterDtoParaEmpresa(cadastroPJDto);
        Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPJDto, result);

        if(result.hasErrors()) {
            log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        this.empresaServices.persistir(empresa);
        funcionario.setEmpresa(empresa);
        this.funcionarioService.persistir(funcionario);
        response.setData(this.converterFuncionarioEmCadastroPJDto(funcionario));

        return ResponseEntity.ok(response);

    }

    /**
     * Converte Dto para empresa pegando alguns dados.
     * @param cadastroPJDto
     * @return empresa
     */
    private Empresa converterDtoParaEmpresa(CadastroPJDto cadastroPJDto) {
        Empresa empresa = new Empresa();
        empresa.setCnpj(cadastroPJDto.getCnpj());
        empresa.setRazaoSocial(cadastroPJDto.getRazaoSocial());
        return empresa;
    }

    /**
     * Verifica se a empresa ou funcionario já existem na base de dados.
     * @param cadastroPJDto
     * @param result
     */
    private void validarDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result){
        this.empresaServices.buscarPorCnpj(cadastroPJDto.getCnpj())
                .ifPresent(empresa -> result.addError(new ObjectError("empresa", "Empresa já existente")));

        this.funcionarioService.buscarPorCpf(cadastroPJDto.getCpf())
                .ifPresent(fun -> result.addError(new ObjectError("funcionario", "CPF já existente")));

        this.funcionarioService.buscarPorEmail(cadastroPJDto.getEmail())
                .ifPresent(fun -> result.addError(new ObjectError("funcionario", "Email já existente")));
    }

    /**
     * Conveter CadastroPjDto para funcionario
     * @param cadastroPJDto
     * @param result
     * @return Funcionario
     * @throws NoSuchAlgorithmException
     */
    private Funcionario converterDtoParaFuncionario(CadastroPJDto cadastroPJDto, BindingResult result) throws NoSuchAlgorithmException{
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(cadastroPJDto.getNome());
        funcionario.setEmail(cadastroPJDto.getEmail());
        funcionario.setCpf(cadastroPJDto.getCpf());
        funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
        funcionario.setSenha(PasswordUtils.gerarBcrypt(cadastroPJDto.getSenha()));

        return funcionario;
    }

    /**
     * Popula Dto de cadastro com os dados do funcionario e empresa.
     * @param funcionario
     * @return CadastroPJDto
     */
    private CadastroPJDto converterFuncionarioEmCadastroPJDto(Funcionario funcionario){
        CadastroPJDto cadastroPJDto = new CadastroPJDto();
        cadastroPJDto.setId(funcionario.getId());
        cadastroPJDto.setNome(funcionario.getNome());
        cadastroPJDto.setEmail(funcionario.getEmail());
        cadastroPJDto.setCpf(funcionario.getCpf());
        cadastroPJDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
        cadastroPJDto.setCnpj(funcionario.getEmpresa().getCnpj());

        return cadastroPJDto;
    }
}
