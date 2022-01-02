package org.bardales.junit.ejemplos.models;

import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bardales.junit.ejemplos.exceptions.DineroInsuficienteException;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Cuenta {

    @NonNull
    private String persona;
    @NonNull
    private BigDecimal saldo;

    private Banco banco;

    public void debito(BigDecimal monto) {
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Dinero insuficiente");
        }
        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }

}
