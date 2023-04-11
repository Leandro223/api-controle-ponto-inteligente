package com.baracho.api.controleponto.repositories;

import com.baracho.api.controleponto.entities.Empresa;
import com.baracho.api.controleponto.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Transactional(readOnly = true)
    Empresa findByCnpj(String cnpj);

    @Query("SELECT f FROM Funcionario f WHERE f.empresa.cnpj = :cnpj")
    List<Funcionario> findFuncionariosByCnpj(String cnpj);
}
