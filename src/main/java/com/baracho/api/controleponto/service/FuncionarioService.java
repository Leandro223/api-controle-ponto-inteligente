package com.baracho.api.controleponto.service;

import com.baracho.api.controleponto.entities.Funcionario;

import java.util.Optional;

public interface FuncionarioService {

    /**
     * Persiste um funcionario na base de dados
     * @param funcionario
     * @return Funcionaario
     */
    Funcionario persistir (Funcionario funcionario);

    /**
     * Busca e retorna um funcionario dado um cpf
     * @param cpf
     * @return Optional<Funcionario>
     */
    Optional<Funcionario> buscarPorCpf(String cpf);

    /**
     * Busca e retorna um funcionario dado um id
     * @param id
     * @return Optional<Funcionario>
     */
    Optional<Funcionario> buscarPorId(Long id);

    /**
     * Busca e retorna um funcioanrio dado um email
     * @param email
     * @return Optional<Funcionario>
     */
    Optional<Funcionario> buscarPorEmail(String email);
}
