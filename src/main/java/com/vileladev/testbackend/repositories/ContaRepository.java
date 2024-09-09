package com.vileladev.testbackend.repositories;

import com.vileladev.testbackend.entities.Conta;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumeroConta(Long numeroConta);

    @Modifying
    @Transactional
    @Query("update Conta c set c.saldo = :saldo where c.numeroConta = :numeroConta")
    int updateSaldoByNumeroConta(@Param("saldo") BigDecimal saldo, @Param("numeroConta") Long numeroConta);
}
