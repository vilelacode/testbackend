package com.vileladev.testbackend.repositories;

import com.vileladev.testbackend.entities.Extrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExtratoRepository extends JpaRepository<Extrato, Long> {

    Optional<Extrato> findByContaNumeroConta(Long numeroConta);

}
