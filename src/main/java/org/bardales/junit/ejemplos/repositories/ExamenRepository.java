package org.bardales.junit.ejemplos.repositories;

import java.util.List;
import org.bardales.junit.ejemplos.models.Examen;

public interface ExamenRepository {

    Examen guardar(Examen examen);

    List<Examen> findAll();

}
