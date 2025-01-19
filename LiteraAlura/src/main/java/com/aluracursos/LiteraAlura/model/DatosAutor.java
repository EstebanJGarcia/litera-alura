package com.aluracursos.LiteraAlura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosAutor(
        @JsonAlias("name") String name,
        @JsonAlias("birth_year") Integer birthday,
        @JsonAlias("death_year") Integer deathday
) {
}
