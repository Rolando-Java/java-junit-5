package org.bardales.junit.ejemplos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.bardales.junit.ejemplos.Data;
import org.bardales.junit.ejemplos.models.Examen;
import org.bardales.junit.ejemplos.repositories.ExamenRepository;
import org.bardales.junit.ejemplos.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/*
 permite inicializar los campos anotados con @Mock.
 Alternativa a usar el método openMocks()
 */
@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    /*
     esta anotación permite mocker la instancia de una clase.
     Otra alternativa al uso del método mock()
    */
    @Mock
    ExamenRepository examenRepository;

    @Mock
    PreguntaRepository preguntaRepository;

    /*
     esta anotación permite inyectar los mocks incializados, mediante
     inyección de constructor
     */
    @InjectMocks
    ExamenServiceImpl examenService;

    @BeforeEach
    void beforeEach() {
        /*
        el método openMocks() permite inicializar los campos
        anotados con @Mock
        */
//        MockitoAnnotations.openMocks(this);

//        this.examenRepository = mock(ExamenRepository.class);
//        this.preguntaRepository = mock(PreguntaRepository.class);
//        this.examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);
    }

    @Tag("examen")
    @Nested
    @DisplayName("Test exmamen")
    class ExamenTest {

        @Test
        @DisplayName("buscando nombre de examen en una lista")
        void findExamenPorNombre() {
            List<Examen> datos = Data.EXAMENES;
        /*
         determina la información a retornar cuando se invoque el método
        */
            when(examenRepository.findAll()).thenReturn(datos);

            Optional<Examen> examenOptional = examenService.findExamenPorNombre("Matemáticas");

            assertTrue(examenOptional.isPresent(), () -> "no se encontro el examen");
            assertEquals(5L, examenOptional.orElseThrow().getId(),
                    () -> "el id del examen no es el esperado");
            assertEquals("Matemáticas", examenOptional.orElseThrow().getNombre(),
                    "el nombre del examen no es el esperado");

        }

        @Test
        @DisplayName("buscando nombre de examen en lista vacia")
        void findExamenPorNombreListaVacia() {
            List<Examen> datos = Collections.emptyList();
            when(examenRepository.findAll()).thenReturn(datos);

            Optional<Examen> examenOptional = examenService.findExamenPorNombre("Matemáticas");

            assertFalse(examenOptional.isPresent(), () -> "si se encontro el examen");
        }

        @Test
        @DisplayName("probando preguntas examen")
        void testPreguntasExamen() {
            List<Examen> datos = Data.EXAMENES;
            when(examenRepository.findAll()).thenReturn(datos);
            List<String> preguntas = Data.PREGUNTAS;
        /*
         haciendo uso de any, para indicar que haga match con cualquier
         valor de argumento de tipo Long que pueda recibir dicho método
        */
            when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

            Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

            assertNotNull(examen, () -> "no se encontro el examen");
            assertEquals(5L, examen.getPreguntas().size(),
                    () -> "la cantidad de preguntas no es el esperado");
            assertTrue(examen.getPreguntas().contains("integrales"),
                    () -> "la pregunta no se encontro");
        }

        @Test
        @DisplayName("probando verify de preguntas de examen")
        void testPreguntasExamenVerify() {
            List<Examen> datos = Data.EXAMENES;
            when(examenRepository.findAll()).thenReturn(datos);
            List<String> preguntas = Data.PREGUNTAS;
            when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);

            Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

            assertNotNull(examen, () -> "no se encontro el examen");
            assertEquals(5L, examen.getPreguntas().size(),
                    () -> "la cantidad de preguntas no es el esperado");
            assertTrue(examen.getPreguntas().contains("integrales"),
                    () -> "la pregunta no se encontro");

        /*
         mediante el método verify, se verifica si se han
         invocado los métodos mocks de dicha clase
        */
            verify(examenRepository).findAll();
            verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
        }

        @Test
        @DisplayName("probando verify de examen que no existen")
        void testNoExisteExamenVerify() {
            when(examenRepository.findAll()).thenReturn(Collections.emptyList());
            List<String> preguntas = Data.PREGUNTAS;

            Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

            assertNull(examen, () -> "se encontro el examen");

        /*
         mediante el método verify, se verifica si se ha invocado
          el metodo durante la ejecucion de test
        */
            verify(examenRepository).findAll();
        }

        @Test
        @DisplayName("probando guardar examen")
        void testGuardarExamen() {
            //Given
            Examen newExamen = Data.EXAMEN;
            newExamen.setPreguntas(Data.PREGUNTAS);

            final AtomicLong secuencia = new AtomicLong(8);
            /*
             a diferencia del metodo thenReturn, el metodo then nos permite
             hacer uso de la interface Answer para interactuar con el
             valor que va a retornar el mockito
            */
            when(examenRepository.guardar(any(Examen.class))).then(invocationOnMock -> {
                Examen examen = invocationOnMock.getArgument(0, Examen.class);
                examen.setId(secuencia.getAndIncrement());
                return examen;
            });

            // When
            Examen examen = examenService.guardar(newExamen);

            //Then
            assertEquals(8L, examen.getId(), () -> "el indice del examen no es el esperado");
            assertEquals("Fisica", examen.getNombre(),
                    () -> "el nombre del examen no es el esperado");

            verify(examenRepository).guardar(any(Examen.class));
            verify(preguntaRepository).guardarVarias(anyList());
        }
    }

}
