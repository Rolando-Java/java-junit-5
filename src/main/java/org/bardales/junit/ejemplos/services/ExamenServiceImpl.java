package org.bardales.junit.ejemplos.services;

import java.util.List;
import java.util.Optional;
import org.bardales.junit.ejemplos.models.Examen;
import org.bardales.junit.ejemplos.repositories.ExamenRepository;
import org.bardales.junit.ejemplos.repositories.PreguntaRepository;

public class ExamenServiceImpl implements ExamenService {

    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository,
            PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(final String nombre) {
        return this.examenRepository.findAll().stream()
                .filter(examen -> examen.getNombre().equals(nombre)).findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = this.findExamenPorNombre(nombre);
        Examen examen = null;
        if (examenOptional.isPresent()) {
            examen = examenOptional.orElseThrow();
            List<String> preguntas = this.preguntaRepository.findPreguntasPorExamenId(
                    examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if (!examen.getPreguntas().isEmpty()) {
            this.preguntaRepository.guardarVarias(examen.getPreguntas());
        }
        return this.examenRepository.guardar(examen);
    }

}
