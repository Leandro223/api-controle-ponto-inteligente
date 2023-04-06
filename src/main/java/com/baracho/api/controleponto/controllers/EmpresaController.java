package com.baracho.api.controleponto.controllers;

import com.baracho.api.controleponto.dto.EmpresaDto;
import com.baracho.api.controleponto.entities.Empresa;
import com.baracho.api.controleponto.response.Response;
import com.baracho.api.controleponto.service.EmpresaServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/empresas")
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
