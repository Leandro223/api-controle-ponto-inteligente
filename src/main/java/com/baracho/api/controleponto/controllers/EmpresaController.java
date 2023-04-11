package com.baracho.api.controleponto.controllers;

import com.baracho.api.controleponto.dto.EmpresaDto;
import com.baracho.api.controleponto.dto.FuncionarioDto;
import com.baracho.api.controleponto.entities.Empresa;
import com.baracho.api.controleponto.entities.Funcionario;
import com.baracho.api.controleponto.response.Response;
import com.baracho.api.controleponto.service.EmpresaServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    private static final Logger log = LoggerFactory.getLogger(EmpresaController.class);

    @Autowired
    EmpresaServices empresaServices;

    /**
     * Retorna uma empresa dado um cnpj
     * @param cnpj
     * @return ResponseEntity<Response<EmpresaDto>>
     */
    @GetMapping(value = "/cnpj/{cnpj}")
    public ResponseEntity<Response<EmpresaDto>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
        log.info("buscando empresa por cnpj: {}", cnpj);
        Response<EmpresaDto> response = new Response<EmpresaDto>();
        Optional<Empresa> empresa = this.empresaServices.buscarPorCnpj(cnpj);
        if (!empresa.isPresent()) {
            log.info("Empresa não encontrada para o cnpj: {}", cnpj);
            response.getErrors().add("empresa não encontrada para o cnpj " + cnpj);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(this.converterEmpresaEmpresaDto(empresa.get()));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/cnpj/funcionarios/{cnpj}")
    public ResponseEntity<List<FuncionarioDto>> buscarFuncionariosPorCnpj(@PathVariable("cnpj") String cnpj) {
        log.info("Buscando Funcionarios por CNPJ: {}", cnpj);
        Optional<Empresa> empresa = this.empresaServices.buscarPorCnpj(cnpj);
        if(!empresa.isPresent()){
            log.info("Empresa não encontrada para o cnpj: {}", cnpj);
            return ResponseEntity.badRequest().build();
        }
        List<FuncionarioDto> funcionarioDto = empresa.get().getFuncionarios().stream()
                .map(this::conveterFuncionarioParaFuncionarioDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(funcionarioDto);
    }

    private FuncionarioDto conveterFuncionarioParaFuncionarioDto(Funcionario funcionario) {
        FuncionarioDto funcionarioDto = new FuncionarioDto();
        funcionarioDto.setId(funcionario.getId());
        funcionarioDto.setNome(funcionario.getNome());
        funcionarioDto.setEmail(funcionario.getEmail());
        return funcionarioDto;
    }


    /**
     * Popula um DTO com os dados de uma empresa
     * @param empresa
     * @return EmpresaDto
     */
    private EmpresaDto converterEmpresaEmpresaDto(Empresa empresa){
        EmpresaDto empresaDto = new EmpresaDto();
        empresaDto.setId(empresa.getId());
        empresaDto.setCnpj(empresa.getCnpj());
        empresaDto.setRazaoSocial(empresa.getRazaoSocial());
        return empresaDto;
    }
}
