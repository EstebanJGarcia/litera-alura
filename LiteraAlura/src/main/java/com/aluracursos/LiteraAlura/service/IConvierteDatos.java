package com.aluracursos.LiteraAlura.service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}

