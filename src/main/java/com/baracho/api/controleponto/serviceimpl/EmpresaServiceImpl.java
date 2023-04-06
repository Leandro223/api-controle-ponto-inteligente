package com.baracho.api.controleponto.serviceimpl;

import com.baracho.api.controleponto.entities.Empresa;
import com.baracho.api.controleponto.repositories.EmpresaRepository;
import com.baracho.api.controleponto.service.EmpresaServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmpresaServiceImpl implements EmpresaServices {

    private static final Logger log = LoggerFactory.getLogger(EmpresaServiceImpl.class);

    @Autowired
    EmpresaRepository empresaRepository;

    @Override
    public Optional<Empresa> buscarPorCnpj(String cnpj) {
        log.info("Buscando uma empresa para o cnpj {}", cnpj);
        return Optional.ofNullable(empresaRepository.findByCnpj(cnpj));
    }

    @Override
    public Empresa persistir(Empresa empresa) {
        log.info("Persistindo uma empresa: {}", empresa);
        return this.empresaRepository.save(empresa);
    }
}
