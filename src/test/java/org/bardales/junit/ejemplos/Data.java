package org.bardales.junit.ejemplos;

import java.util.List;
import org.bardales.junit.ejemplos.models.Examen;

public class Data {

    public static final List<Examen> EXAMENES = List.of(new Examen(5L, "Matemáticas"),
            new Examen(6L, "Lenguaje"), new Examen(7L, "Historia"));

    public static final List<String> PREGUNTAS = List.of("aritmética", "integrales", "derivadas",
            "trigonometría", "geometría");

    public static final Examen EXAMEN = new Examen(null, "Fisica");

}
