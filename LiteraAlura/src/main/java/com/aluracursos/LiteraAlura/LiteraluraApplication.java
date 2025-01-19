package com.aluracursos.LiteraAlura;

import com.aluracursos.LiteraAlura.principal.Principal;

import com.aluracursos.LiteraAlura.repository.AutorRepository;
import com.aluracursos.LiteraAlura.repository.LibroRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private LibroRepository libroRepository;

	@Autowired
	private AutorRepository autorRepository;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
		Principal principal = new Principal(libroRepository, autorRepository);
		principal.muestraElMenu();
	}

}