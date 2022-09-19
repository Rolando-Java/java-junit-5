package org.bardales.junit.ejemplos.models;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Examen {

    private Long id;
    private String nombre;
    private List<String> preguntas;

    public Examen(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.preguntas = new ArrayList<>();
    }

}
