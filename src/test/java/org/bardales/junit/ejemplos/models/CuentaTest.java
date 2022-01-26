package org.bardales.junit.ejemplos.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.bardales.junit.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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

    private TestInfo testInfo;
    private TestReporter testReporter;

    /*
     esta anotacion indica que el metodo se ejecutara solo una vez durante la compilacion de la clase
     y antes que se creen las instancias de esta clase
     */
    @BeforeAll
    static void beforeAll() {
        System.out.println("incializando el test");
    }

    /*
     esta anotacion indica que el metodo se ejecutara solo una vez durante la compilacion de la clase
     y despues que se ejecuten las instancias de esta clase
     */
    @AfterAll
    static void afterAll() {
        System.out.println("finalizando el test");
    }

    private static List<String> montoList() {
        return List.of("100", "200", "300", "500", "700", "1000.12345");
    }

    /*
     esta anotacion indica que se ejecutara al inicio de cada instancia de esta clase. Es decir, por
     cada test unitario que se ejecute. Cabe resaltar, que se ejecutara antes del metodo test
     */
    @BeforeEach
    void beforeEach(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));

        /*
         el objeto TestInfo haciendo uso de reflexion nos permite acceder a la informacion que
         un metodo unitario pueda tener, como por ejemplo: su displayname, el nombre de la clase
         al que pertenece, el nombre del metodo, los tags que pueda tener el o la clase que lo
         contiene, etc.
         */
        this.testInfo = testInfo;
        /*
         el objeto TestReporter permite imprimir el mensaje, a través de la salida
         del sistema log de junit platform. En donde se puede apreciar que ademas del mensaje
         se imprime la fecha en que ocurrio
        */
        this.testReporter = testReporter;
        System.out.println("Iniciando el metodo.");
        System.out.println("Iniciando el metodo.");

        this.testReporter.publishEntry(
                "ejecutando : " + testInfo.getDisplayName() + " nombre clase : "
                        + testInfo.getTestClass().map(Class::getSimpleName).orElse("")
                        + " , nombre metodo : " + testInfo.getTestMethod().map(Method::getName)
                        .orElse("") + " con las etiquetas : " + testInfo.getTags());
    }

    /*
     esta anotacion indica que se ejecutara al final de cada instancia de esta clase. Es decir, por
     cada test unitario que se ejecute. Cabe resaltar, que se ejecutara despues del metodo test
     */
    @AfterEach
    void afterEach() {
        System.out.println("finalizando el metodo");
    }

    /*
     esta anotacion nos permite definir palabras clave a cada metodo o clase que agrupe metodos
     test. De forma que podamos ejecutar solo aquellos que tengan como tag una palabra
     clave en especifico
    */
    @Tag("cuenta")
    @Tag("error")
    /*
     anotacion que define que el metodo es una prueba unitaria
     */
    @Test
    void testDineroInsuficienteException() {
        /*
         si se cumple que la condicion de que la excepcion lanzada pertenece
         a dichas clase, entonces se retorna la excepcion
         */
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            this.cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Tag("cuenta")
    @Tag("banco")
    @Test
    /*
     esta anotacion desahbilita la ejecucion del test
     */
    @Disabled
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

        /*
         El metodo assertAll ejecuta todos los assertions que contiene sin importar si uno dea error.
         Posteriormente, muestra un reporte de errores de los assertions, en caso hubiera.
         */
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

    /*
     esta anotacion te permite repetir la ejecucion de un test mas de una vez
     */
    // haciendo uso de las expresiones para modificar el mensaje de cada repeticion
    @RepeatedTest(value = 5, name = "{displayName} - " + "Repeticion numero {currentRepetition} de "
            + "{totalRepetitions}")
    @DisplayName("probando Debito Cuenta Repetir")
    void testDebitoCuentaRepetir(RepetitionInfo info) {
        /*
         mediante la clase RepetitionInfo podemos ejecutar
         diferente codigo segun el numero de repeticion
         en el que nos encontremos
        */
        if (info.getCurrentRepetition() == 3) {
            System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
        }

        this.cuenta.debito(new BigDecimal(100));

        /*
         setear los mensajes de error con expresiones lambda, para lograr que se instancien
         en caso no se cumpla los assertions
        */
        assertNotNull(this.cuenta.getSaldo(), () -> "el valor del saldo es nulo");
        assertEquals(900, this.cuenta.getSaldo().intValue(),
                () -> "el valor del saldo no es el esperado");
        assertEquals("900.12345", this.cuenta.getSaldo().toPlainString(),
                () -> "el valor del saldo no es el esperado");
    }

    @Tag("param")
    /*
     esta anotacion nos permite parametrizar los test
     */
    @ParameterizedTest(name = "{displayName} - numero {index} ejecutando con valor {argumentsWithNames}")
    /*
     esta anotacion lama a un metodo para obtener los parametros. El metodo debe ser static
     */
    @MethodSource("montoList")
    @DisplayName("test Debito Cuenta Parametrizado con Method Source")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));

        assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0,
                () -> "es falso que el saldo es mayor que 0");
    }

    @Test
    void testJdkDev() {
        boolean esJdk17Dev = "16".equals(System.getProperty("java.specification.version"));
        /*
         si se cumple la condicion del assummeTrue, entonces se ejecutara las demas instrucciones
         que le siguen a este. Caso contrario, se deshabilitara y abortara la ejecucion del test,
          y no se ejecutara las demas instrucciones.
         */
        assumeTrue(esJdk17Dev, () -> "no se cumplio con la version del jdk");
        assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
        assertEquals(1000.12345D, cuenta.getSaldo().doubleValue(),
                () -> "el valor del saldo no es el esperado");
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0,
                () -> "el valor del saldo es menor 0");
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0,
                () -> "el valor del saldo es menor o igual a 0");
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
            assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
            assertEquals(1000.12345D, cuenta.getSaldo().doubleValue(),
                    () -> "el valor del saldo no es el esperado");
        });

        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0,
                () -> "el valor del saldo es menor que 0");
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0,
                () -> "el valor del saldo es menor o igual a 0");

    }

    @Tag("timeout")
    @Nested
    @DisplayName("Test timeout")
    class EjemploTimeoutTest {

        @Test
        /*
         esta anotacion controla que la ejecucion no
         demore mas del tiempo indicado. Caso contrari se retornara
         una excepcion de TimeoutException
         */
        @Timeout(1)
        @DisplayName("en segundos")
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        @DisplayName("en milisegundos")
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void testTimeoutAssertions() {
            /*
             este assert procura condiciona a no pasarse del tiempo indicado.
             Caso contrario, se botara el mensaje de error.
            */
            assertTimeout(Duration.ofSeconds(5), () -> TimeUnit.MILLISECONDS.sleep(4900),
                    () -> "la tarea demoro mas del tiempo debido");
        }

    }

    @Tag("param")
    /*
     anotacion que permite a la inner class agrupar test unitarios
     */
    @Nested
    /*
     define la descripcion de la clase que agrupa los test
     */
    @DisplayName("Test Debito Cuenta Parametrizado")
    class PruebasParametrizadas {

        @ParameterizedTest(name = "{displayName} - numero {index} ejecutando con valor {argumentsWithNames}")
        /*
         esta anotacion define un arreglo de valores de un tipo en especifico, para luego
         pasarlo como parametro
         */
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.12345"})
        /*
         define la descripcion del test
         */
        @DisplayName("value source")
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0,
                    () -> "es falso que el saldo es mayor que 0");
        }

        @ParameterizedTest(name = "{displayName} - numero {index} ejecutando con valor {argumentsWithNames}")
        /*
         esta anotacion define valores tipo csv que son separados por comas, para luego
         pasarlos a cada parametro definido en el metodo
         */
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345"})
        @DisplayName("csv source")
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0,
                    () -> "es falso que el saldo es mayor que 0");
        }

        @ParameterizedTest(name = "{displayName} - numero {index} ejecutando con valor {argumentsWithNames}")
        @CsvSource({"200,100,John,Andres", "250,200,Pepe,Pepe", "300,300,maria,Maria",
                "510,500,Pepa,Pepa", "750,700,Lucas,Luca", "1000.12345,1000.12345,Cata,Cata"})
        @DisplayName("csv source 2")
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado,
                String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
            assertNotNull(cuenta.getPersona(), () -> "el nombre de la persona es nulo");
            assertEquals(esperado, actual,
                    () -> "el nombre del duenio de la cuenta no es el esperado");
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0,
                    () -> "es falso que el saldo es mayor que 0");
        }

        @ParameterizedTest(name = "{displayName} - numero {index} ejecutando con valor {argumentsWithNames}")
        /*
         esta anotacion llama a un archivo csv que define valores por comas, para luego
         pasarlos a cada parametro definido en el metodo
         */
        @CsvFileSource(resources = "/data.csv")
        @DisplayName("csv file source")
        void testDebitoCuentaCsvFileSource(int index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0,
                    () -> "es falso que el saldo es mayor que 0");
        }

        @ParameterizedTest(name = "{displayName} - numero {index} ejecutando con valor {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        @DisplayName("csv file source 2")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado,
                String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(esperado);

            assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
            assertNotNull(cuenta.getPersona(), () -> "el nombre de la persona es nulo");
            assertEquals(esperado, actual,
                    () -> "el nombre del duenio de la cuenta no es el esperado");
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0,
                    () -> "es falso que el saldo es mayor que 0");
        }

    }

    @Tag("cuenta")
    @Nested
    @DisplayName("Probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo {

        @Test
        @DisplayName("el nombre!")
        void testNombreCuenta() {
            if (testInfo.getTags().contains("cuenta")) {
                testReporter.publishEntry("hacer algo con la etiqueta");
            }
            String esperado = "Andres";
            String real = cuenta.getPersona();

            assertNotNull(real, () -> "la cuenta no puede ser nula.");
            assertEquals(esperado, real, () -> String.format(
                    "el nombre de la cuenta no es el que se esperaba: se esperaba %1$s sin embargo fue %2$s .",
                    esperado, real));
            assertTrue(real.equals(esperado),
                    () -> "nombre cuenta esperada debe ser igual a la real.");
        }

        @Test
        @DisplayName("el saldo, que no sea null, mayor que cero, valor esperado.")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
            assertEquals(1000.12345D, cuenta.getSaldo().doubleValue(),
                    () -> "el valor del saldo no es el esperado");
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0,
                    () -> "el valor del saldo es menor que 0");
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0,
                    () -> "el valor del saldo es menor o igual a 0");
        }


        @Test
        @DisplayName("testeando referencias que sean iguales con el metodo equals.")
        void testReferenciaCuenta() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            Cuenta cuentaDos = new Cuenta("John Doe", new BigDecimal("8900.9997"));

            assertEquals(cuentaDos, cuenta, () -> "la cuenta no es el mismo que el esperado");
        }

    }

    @Tag("cuenta")
    @Nested
    @DisplayName("probando movimiento en la cuenta corriente")
    class CuentaOperacionesTest {

        @Test
        @DisplayName("debito")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
            assertEquals(900, cuenta.getSaldo().intValue(),
                    () -> "el valor del saldo no es el esperado");
            assertEquals("900.12345", cuenta.getSaldo().toPlainString(),
                    () -> "el valor del saldo no es el esperado");
        }

        @Test
        @DisplayName("credito")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo(), () -> "el valor del saldo es nulo");
            assertEquals(1100, cuenta.getSaldo().intValue(),
                    () -> "el valor del saldo no es el esperado");
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString(),
                    () -> "el valor del saldo no es el esperado");
        }

        @Tag("banco")
        @Test
        @DisplayName("transferir dinero")
        void testTransferirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                    () -> "el valor del saldo no es el esperado");
            assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                    () -> "el valor del saldo no es el esperado");
        }

    }

    @Nested
    @DisplayName("Test Sistema operativo")
    class SistemaOperativoTest {

        @Test
        /*
         esta anotacion permite activar la ejecucion del test
         en un sistema operativo especifico
         */
        @EnabledOnOs(OS.WINDOWS)
        @DisplayName("activar ejecucion de test para OS windows")
        void testSoloWindows() {

        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        @DisplayName("desactivar ejecucion de test para OS Linux o Mac")
        void testSoloLinuxMac() {

        }

        @Test
        /*
         esta anotacion permite desactivar la ejecucion del test
         en un sistema operativo especifico
         */
        @DisabledOnOs(OS.WINDOWS)
        @DisplayName("desactivar ejecucion de test para OS Windows")
        void testNoWindows() {

        }

    }

    @Nested
    @DisplayName("Test java version")
    class JavaVersionTest {

        @Test
        /*
         esta anotacion permite activar la ejecucion del test
         si se usa una version de JRE especifico
         */
        @EnabledOnJre(JRE.JAVA_8)
        @DisplayName("activar ejecucion para java version 8")
        void testSoloJdk8() {

        }

        @Test
        @EnabledOnJre(JRE.JAVA_17)
        @DisplayName("activar ejecucion para java version 17")
        void testSoloJdk17() {

        }

        @Test
        /*
         esta anotacion permite desactivar la ejecucion del test
         si se usa una version de JRE especifico
         */
        @DisabledOnJre(JRE.JAVA_17)
        @DisplayName("desactivar ejecucion para java version 17")
        void testNoJdk17() {

        }

    }

    @Nested
    @DisplayName("Test variables de propiedades del JVM")
    class SistemPropertiesTest {

        /*
         Las propiedades del sistema de la maquina viritual de java o las
         que se establecen en la linea de comandos utilizando
         la -Dpropertyname=valuesintaxis
        */
        @Test
        @DisplayName("listando variables de propiedades")
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((o, o2) -> System.out.println(o + " " + o2));
        }

        @Test
        /*
         esta anotacion me permite activar la ejecucion del test si hay relacion
         con la variable de propiedad
         */
        @EnabledIfSystemProperty(named = "java.version", matches = "17.0.1")
        @DisplayName("java version para activar ejecucion")
        void testJavaVersion() {

        }

        @Test
        /*
         esta anotacion me permite activar la ejecucion del test si hay relacion
         con la variable de propiedad
         */
        @DisabledIfSystemProperty(named = "os.arch", matches = "\\w*64\\w*")
        @DisplayName("arquitectura OS para desactivar ejecucion")
        void testNo64() {

        }

    }

    @Nested
    @DisplayName("Test variables de ambiente")
    class VariableAmbienteTest {

        /*
         Las variables de entorno se establecen en el sistema operativo
         */
        @Test
        @DisplayName("listando variables de ambiente")
        void imprimirEnviromentVariables() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((s, s2) -> System.out.println(s + " " + s2));
        }

        @Test
        /*
         esta anotacion me permite activar la ejecucion del test si hay relacion
         con la variable de ambiente
         */
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-17.0.1.*")
        @DisplayName("java home para activar ejecucion")
        void testJavaHome() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        @DisplayName("numero de procesadores para activar ejecucion")
        void testProcesadores8() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "USERNAME", matches = "lucas")
        @DisplayName("nombre de usuario para activar ejecucion")
        void testEnv() {

        }

        @Test
        /*
         esta anotacion me permite desactivar la ejecucion del test si hay
         relacion con la variable de ambiente
         */
        @DisabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "16")
        @DisplayName("numero de procesadores para desactivar ejecucion")
        void testNoProcesadores16() {

        }

    }

}
