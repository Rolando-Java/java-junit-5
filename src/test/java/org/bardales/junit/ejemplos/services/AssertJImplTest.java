package org.bardales.junit.ejemplos.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.bardales.junit.ejemplos.models.Examen;
import org.junit.jupiter.api.Test;

class AssertJImplTest {

    /*
        Para aprender más sobre assertJ, ver la documentación en:
        https://joel-costigliola.github.io/assertj/assertj-core-features-highlight.html
     */

    @Test
    void tryAssertsJ() {
        String name = "Mike";
        assertThat(name)
                // as -> agregar una descripción al assert, el cual se mostrará en caso falle
                .as("Check that the name starts with M")
                .startsWith("M");
    }

    @Test
    void tryAssertsJ2() {
        int[] intArray = new int[]{1, 10, 30, 50, 100};
        assertThat(intArray)
                .as("Check length of array and what it contains")
                .hasSize(5)
                .contains(1, 100);
    }

    @Test
    void tryAssertJ3() {
        Optional<String> description = Optional.of("This is a cool framework");
        assertThat(description)
                .as("Check what if it's not empty and what is contains")
                .isNotEmpty()
                .contains("This is a cool framework");
    }

    @Test
    void tryAssertJ4() {
        boolean isNumber = "123".matches("^\\d+$");
        assertThat(isNumber)
                .as("Check if the value is number")
                .isTrue();
    }

    @Test
    void tryAssertJ5() {
        Examen examen = new Examen(1L, "joe");
        examen.getPreguntas().add("question1");
        examen.getPreguntas().add("question2");

        Examen examen2 = new Examen(1L, "joe");
        examen2.getPreguntas().add("question1");
        examen2.getPreguntas().add("question2");

        assertThat(examen)
                /*
                  Valida que los dos objetos sean iguales, haciendo uso del método equals().
                  Debes sobreescribir este método, para que valide la igualdad del contenido y no
                  de la referencia
                 */
                .isEqualTo(examen2);
        assertThat(examen)
                /*
                 Valida que la referencia de ambos objetos sea el mismo. Es decir, es como un ==
                 */
                .isSameAs(examen);
    }

    @Test
    void tryAssertsSoft() {
        /*
         Usando aserciones blandas, AssertJ recopila todos los errores de aserción, en lugar
         de detenerse en el primero que falle.
         Usando aserciones blandas, puede recopilar todas las aserciones fallidas.
         */
        int[] intArray = new int[]{1, 10, 30, 50, 100, 123, 4, 5, 6, 7, 8};
        String name = "Mike";
        String name2 = "Susanne";
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(intArray).hasSizeBetween(3, 12);
        soft.assertThat(name).isLessThan(name2);
        soft.assertAll();
    }


}
