package org.bardales.junit.ejemplos.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import org.bardales.junit.ejemplos.util.AllStatic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class StaticMethodMockImplTest {

    @Test
    @DisplayName(("test static method format name"))
    void testStaticMethodFormatName() {
        /*
         Es necesario llamar al ScopedMock.close() método para liberar el simulacro estático una
         vez que se halla utilizado y ya no sea necesario. Por lo tanto, se crea el objeto en un
         try with resources.
         */
        try (MockedStatic mockedStatic = Mockito.mockStatic(AllStatic.class)) {
            mockedStatic.when(() -> AllStatic.formatName(anyString())).thenReturn("LEO");

            assertThat(AllStatic.formatName("leo")).isEqualTo("LEO");
            mockedStatic.verify(() -> AllStatic.formatName(anyString()));
        }
        // fuera del ámbito del try, es como invocar directamente el método de la clase estática.
        assertThat(AllStatic.formatName("leo")).contains("good day");
    }

    @Test
    @DisplayName("test static method say hello static")
    void testStaticMethodSayHelloStatic() {
        try (MockedStatic mockedStatic = Mockito.mockStatic(AllStatic.class)) {
            mockedStatic.when(AllStatic::sayHelloStatic).thenReturn("Hello World");

            assertThat(AllStatic.sayHelloStatic()).isEqualTo("Hello World");
            mockedStatic.verify(AllStatic::sayHelloStatic);
        }
        assertThat(AllStatic.sayHelloStatic()).isEqualTo("Static hello");
    }

    @Test
    @DisplayName("test static method send greeting")
    void testStaticMethodSendGreeting() {
        /*
         Mediante Mockito.withSettings(), estamos indicando que se harán llamadas reales
         sobre los métodos que se invoquen dentro de la clase estática. Siempre y cuando,
         no hagamos un stub de la llamada de dichos métodos.
         */
        try (MockedStatic mockedStatic = Mockito.mockStatic(AllStatic.class,
                Mockito.withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {
            // haciendo un stub sobre la llamada del método estático sayHelloStatic()
            mockedStatic.when(AllStatic::sayHelloStatic).thenReturn("Leo");

            assertThat(AllStatic.sendGreeting()).contains("Hello LEO");
            mockedStatic.verify(AllStatic::sayHelloStatic);
            mockedStatic.verify(() -> AllStatic.formatName("Leo"));
            mockedStatic.verify(AllStatic::sendGreeting);
        }

    }

}
