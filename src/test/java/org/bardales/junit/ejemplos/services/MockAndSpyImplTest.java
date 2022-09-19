package org.bardales.junit.ejemplos.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.bardales.junit.ejemplos.models.Examen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/*
 Diferencias entre un Mock y un Spy
 Mock -> estás creando un objeto simulado o falso completo.
 Spy -> estás usando la instancia de un objeto real, y solo estás simulando
        métodos específicos del mismo.
 */
@ExtendWith(MockitoExtension.class)
class MockAndSpyImplTest {

    @Mock
    private EmailService emailServiceMock;
    @Spy
    private EmailService emailServiceSpy = new EmailServiceImpl();
    @Mock
    private Examen examMock;
    @Spy
    private Examen examSpy = new Examen(1L, "leo");

    @Test
    @DisplayName("test mock email with stub")
    void testMockEmailWithStub() {
        when(emailServiceMock.sendMail(anyString())).thenReturn("success");
        String result = emailServiceMock.sendMail("susan@gmail.com");
        assertThat(result).isEqualTo("success");
    }

    @Test
    @DisplayName("test mock email without stub")
    void testMockEmailWithoutStub() {
        /*
         Por defecto la llamada a los métodos de una clase mockeada no hacen nada.
         Es por ello que si no haces stub de la llamada de dichos métodos el resultado
         de estes será nulo.
         */
        String result = emailServiceMock.sendMail("susan@gmail.com");
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("test spy email without stub")
    void testSpyEmailWithoutStub() {
        /*
         En un objeto espía se llama al real método del objeto.
         */
        String result = emailServiceSpy.sendMail("susan@gmail.com");
        assertThat(result).contains("susan@gmail.com");
    }

    @Test
    @DisplayName("test spy email with stub")
    void testSpyEmailWithStub() {
        /*
         Puedes hacer stub de la llamada del método, para que no se llame al método
         real del objeto.
         */
        when(emailServiceSpy.sendMail(anyString())).thenReturn("success");
        String result = emailServiceSpy.sendMail("susan@gmail.com");
        assertThat(result).isEqualTo("success");
    }

    @Test
    @DisplayName("test mock exam with stub")
    void testMockExamWithStub() {
        when(examMock.getNombre()).thenReturn("leo");
        assertThat(examMock.getNombre()).isEqualTo("leo");
    }

    @Test
    @DisplayName("test mock exam without stub")
    void testMockExamWithoutStub() {
        assertThat(examMock.getNombre()).isNull();
    }

    @Test
    @DisplayName("test spy exam without stub")
    void testSpyExamWithoutStub() {
        assertThat(examSpy.getNombre()).isEqualTo("leo");
    }

    @Test
    @DisplayName("test spy exam with stub")
    void testSpyExamWithStub() {
        when(examSpy.getNombre()).thenReturn("su");
        assertThat(examSpy.getNombre()).isEqualTo("su");
    }

}
