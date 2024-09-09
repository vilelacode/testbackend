package com.vileladev.testbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="contas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long numeroConta;

    @Column(nullable = false)
    private String nomeTitular;

    @Column(nullable = false)
    private BigDecimal saldo;

}
