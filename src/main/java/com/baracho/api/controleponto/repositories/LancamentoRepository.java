package com.baracho.api.controleponto.repositories;

import com.baracho.api.controleponto.entities.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
@NamedQueries({
          @NamedQuery(name = "LancamentoRepository.findByFuncionarioId",
                  query = "SELECT lanc FROM Lancamento lanc WHERE lanc.funcionarioId = :funcionarioId")})
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

      List<Lancamento> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);

      Page<Lancamento> findByFuncionario(@Param("funcionarioId") Long funcionarioId, Pageable pageable);

      @Query("SELECT l FROM Lancamento l WHERE l.data BETWEEN :dataInicio AND :datafFim")
      List<Lancamento> buscarPorData(@Param("dataInicio")LocalDate dataInicio, @Param("dataFim")LocalDate dataFim);
}
