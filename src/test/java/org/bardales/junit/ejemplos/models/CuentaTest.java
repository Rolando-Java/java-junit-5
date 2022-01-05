package org.bardales.junit.ejemplos.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;
import org.bardales.junit.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

/*
@TestInstance(Lifecycle.PER_METHOD) --> (por defecto) e indica que se crea una instancia por cada
ejecucion de test

@TestInstance(Lifecycle.PER_CLASS) --> indica que se crea una sola instancia de la clase
para la ejecucion de todos los test. Es decir, que tanto BeforeAll y AfterAll ya no deben ser
metodos estaticos
 */
//@TestInstance(Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    @BeforeAll
    static void beforeAll() {
        System.out.println("incializando el test");
    }

    @AfterAll
    static void afterAll() {
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

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testSoloWindows() {

    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testSoloLinuxMac() {

    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testNoWindows() {

    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testSoloJdk8() {

    }

    @Test
    @EnabledOnJre(JRE.JAVA_17)
    void testSoloJdk17() {

    }

    @Test
    @DisabledOnJre(JRE.JAVA_17)
    void testNoJdk17() {

    }

    /*
     Las propiedades del sistema de la maquina viritual de java o las
     que se establecen en la linea de comandos utilizando
     la -Dpropertyname=valuesintaxis
     */
    @Test
    void imprimirSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((o, o2) -> System.out.println(o + " " + o2));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = "17.0.1")
    void testJavaVersion() {

    }

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = "\\w*64\\w*")
    void testNo64() {

    }

    /*
     Las variables de entorno se establecen en el sistema operativo
     */
    @Test
    void imprimirEnviromentVariables() {
        Map<String, String> getenv = System.getenv();
        getenv.forEach((s, s2) -> System.out.println(s + " " + s2));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-17.0.1.*")
    void testJavaHome() {

    }

    @Test
    @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
    void testProcesadores8() {

    }

    @Test
    @EnabledIfEnvironmentVariable(named = "USERNAME", matches = "lucas")
    void testEnv() {

    }

    @Test
    @DisabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "16")
    void testNoProcesadores16() {

    }

    @Test
    void testJdkDev() {
        boolean esJdk17Dev = "17".equals(System.getProperty("java.specification.version"));
        /*
         si se cumple la condicion del assummeTrue, entonces se ejecutara las demas instrucciones
         que le siguen a este. Caso contrario, se deshabilitara y abortara la ejecucion del test,
          y no se ejecutara las demas instrucciones.
         */
        assumeTrue(esJdk17Dev);
        assertNotNull(this.cuenta.getSaldo());
        assertEquals(1000.12345D, this.cuenta.getSaldo().doubleValue());
        assertFalse(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testJdkDev2() {
        boolean esJdk17Dev = "17".equals(System.getProperty("java.specification.version"));
        /*
         si se cumple la condicion del asummingThat, entonces se ejecutara las demas instrucciones
         que contiene. Caso contrario, se deshabilitara la ejecucion de las intrucciones que este contiene.
         Sin embargo aunque la condicion no se cumpla las instrucciones que esta fuera del assumingThat
         se ejecutaran
         */
        assumingThat(esJdk17Dev, () -> {
            assertNotNull(this.cuenta.getSaldo());
            assertEquals(1000.12345D, this.cuenta.getSaldo().doubleValue());
        });

        assertFalse(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

    }
}
