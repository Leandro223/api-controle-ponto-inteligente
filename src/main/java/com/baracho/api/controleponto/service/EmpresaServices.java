package com.baracho.api.controleponto.service;

import com.baracho.api.controleponto.entities.Empresa;

import java.util.Optional;

public interface EmpresaServices {

    /**
     * Retorna uma empresa dado um cnpj
     * @param cnpj
     * @return Optional<Empresa>
     */
    Optional<Empresa> buscarPorCnpj(String cnpj);

    /**
     * Cadastra uma empresa na base de dados
     * @param empresa
     * @return Empresa
     */
    Empresa persistir(Empresa empresa);
}
