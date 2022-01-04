package org.bardales.junit.ejemplos.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import org.bardales.junit.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    @BeforeAll
    void beforeAll() {
        System.out.println("incializando el test");
    }

    @AfterAll
    void afterAll() {
        System.out.println("finalizando el test");
    }

    @BeforeEach
    void initMetodoTest() {
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        System.out.println("Iniciando el metodo.");
    }

    @AfterEach
    void tearDown() {
        System.out.println("finalizando el metodo");
    }

    @Test   //Define que el metodo es una prueba unitaria que se va a ejecutar
    @DisplayName("probando el nombre de la cuenta corriente")
        //]Define la descripcion del test
    void testNombreCuenta() {
        String esperado = "Andres";
        String real = this.cuenta.getPersona();

        /*
          Setear los mensaje de error con expresiones lambda, para lograr que solo instancien los mensajes
          en caso no se cumpla los assertions
        */
        assertNotNull(real, () -> "la cuenta no puede ser nula.");
        assertEquals(esperado, real, () -> String.format(
                "el nombre de la cuenta no es el que se esperaba: se esperaba %1$s sin embargo fue %2$s .",
                esperado, real));
        assertTrue(real.equals(esperado), () -> "nombre cuenta esperada debe ser igual a la real.");
    }

    @Test
    @DisplayName("probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado.")
    void testSaldoCuenta() {
        assertNotNull(this.cuenta.getSaldo());
        assertEquals(1000.12345D, this.cuenta.getSaldo().doubleValue());
        assertFalse(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    @Test
    @DisplayName("testeando referencias que sean iguales con el metodo equals.")
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        Cuenta cuentaDos = new Cuenta("John Doe", new BigDecimal("8900.9997"));

        assertEquals(cuentaDos, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        this.cuenta.debito(new BigDecimal(100));

        assertNotNull(this.cuenta.getSaldo());
        assertEquals(900, this.cuenta.getSaldo().intValue());
        assertEquals("900.12345", this.cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        this.cuenta.credito(new BigDecimal(100));

        assertNotNull(this.cuenta.getSaldo());
        assertEquals(1100, this.cuenta.getSaldo().intValue());
        assertEquals("1100.12345", this.cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            this.cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    @Disabled   //Deshabilita la ejecucion del test
    @DisplayName("probando relaciones entre las cuentas y el banco con assertAll.")
    void testRelacionBancoCuentas() {
        fail("forzando error"); //Fuerza el error de test
        final Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        final Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        // el metodo assertAll muestra un reporte de errores, en caso hubiera, de los assertions ejecutados
        assertAll(() -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                        () -> "el valor del saldo de la cuenta 2 no es el esperado."),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                        () -> "el valor del saldo de la cuenta 1 no es el esperado."),
                () -> assertEquals(2, banco.getCuentas().size(),
                        () -> "el banco no tiene las cuentas esperadas."),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre(),
                        () -> "el nombre del banco no es el esperado."),
                () -> assertEquals("Andres", banco.getCuentas().stream().map(Cuenta::getPersona)
                                .filter(s -> s.equals("Andres")).findFirst().get(),
                        () -> "no se encontro a esa persona entre las cuentas del banco."),
                () -> assertTrue(banco.getCuentas().stream().map(Cuenta::getPersona)
                                .anyMatch(s -> s.equals("John Doe")),
                        "no existe esa personas en las cuentas del banco."));


    }

}