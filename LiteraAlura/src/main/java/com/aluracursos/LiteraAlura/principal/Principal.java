package com.aluracursos.LiteraAlura.principal;


import com.aluracursos.LiteraAlura.model.Autor;
import com.aluracursos.LiteraAlura.model.DatosAutor;
import com.aluracursos.LiteraAlura.model.DatosLibro;
import com.aluracursos.LiteraAlura.model.Libro;
import com.aluracursos.LiteraAlura.repository.AutorRepository;
import com.aluracursos.LiteraAlura.repository.LibroRepository;
import com.aluracursos.LiteraAlura.service.ConsumoAPI;
import com.aluracursos.LiteraAlura.service.ConvierteDatos;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Principal {
    private final Scanner scanner = new Scanner(System.in);

    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private final String url = "https://gutendex.com/books/";
    private List<DatosLibro> datosLibros = new ArrayList<>();
    private List<DatosAutor> datosAutores = new ArrayList<>();
    private List<Libro> libros;
    private List<Autor> autores;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();
            System.out.print("> ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer

                // Manejo de opciones con switch
                switch (opcion) {
                    case 1 -> buscarLibroPorNombre();
                    case 2 -> mostrarLibrosRegistrados();
                    case 3 -> mostrarAutoresRegistrados();
                    case 4 -> mostrarAutoresPorFecha();
                    case 5 -> mostrarPorIdiomas();
                    case 6 -> mostrarEstadisticasLibros();
                    case 7 -> mostrarTopLibrosMasDescargados();
                    case 8 -> buscarAutorPorNombre();
                    case 9 -> listarAutoresPorAtributo();
                    case 0 -> System.out.println("Cerrando la aplicación...");
                    default -> System.out.println("Opción inválida. Por favor, elija una opción entre 0 y 9.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine(); // Limpiar el buffer
            }
        }
    }

    private void mostrarMenu() {
        String menu = """
            ======= Menú Principal =======
            1 - Buscar Libro por nombre
            2 - Mostrar Libros registrados
            3 - Mostrar autores registrados
            4 - Mostrar autores vivos por años
            5 - Mostrar por idiomas
            6 - Generar estadísticas de libros
            7 - Mostrar Top 10 libros más descargados
            8 - Buscar autor por nombre
            9 - Listar autores por año de nacimiento o fallecimiento
            
            0 - Salir
            =============================
            """;
        System.out.println(menu);
    }

    private void buscarLibroPorNombre() {
        System.out.println("Escribe el nombre del libro que desea buscar");
        String nombreLibro = scanner.nextLine();

        try {
            String urlFinal = construirURL(nombreLibro);
            System.out.println("URL construida: " + urlFinal);

            String json = consumoAPI.obtenerDatos(urlFinal);
            System.out.println("Respuesta JSON: " + json);

            procesarLibrosDesdeJSON(json);
        } catch (Exception e) {
            System.out.println("Error general en la búsqueda de libros: " + e.getMessage());
        }
    }

    private String construirURL(String nombreLibro) {
        return url + "?search=" + URLEncoder.encode(nombreLibro, StandardCharsets.UTF_8);
    }

    private void procesarLibrosDesdeJSON(String json) {
        try {
            Map<String, Object> rootNode = conversor.obtenerDatos(json, Map.class);
            List<Map<String, Object>> results = (List<Map<String, Object>>) rootNode.get("results");

            if (results == null || results.isEmpty()) {
                System.out.println("No se encontraron libros para el término de búsqueda proporcionado.");
                return;
            }

            results.forEach(result -> {
                try {
                    DatosLibro datosLibro = conversor.obtenerDatos(new ObjectMapper().writeValueAsString(result), DatosLibro.class);
                    Libro libro = new Libro(datosLibro);
                    guardarLibroYAutores(libro, datosLibro.authors());
                } catch (Exception e) {
                    System.out.println("Error al procesar un libro: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.out.println("Error al procesar el JSON de resultados: " + e.getMessage());
        }
    }

    private void guardarLibroYAutores(Libro libro, List<DatosAutor> autores) {
        autores.forEach(datosAutor -> {
            try {
                Autor autor = new Autor(datosAutor);
                if (!autorRepository.existsByName(autor.getName())) {
                    autorRepository.save(autor);
                    System.out.println("Autor guardado correctamente: " + autor.getName());
                } else {
                    System.out.println("El autor ya existe en la base de datos: " + autor.getName());
                }
            } catch (Exception e) {
                System.out.println("Error al guardar el autor: " + e.getMessage());
            }
        });

        try {
            libroRepository.save(libro);
            System.out.println("Libro guardado correctamente: " + libro.getTitle());
        } catch (Exception e) {
            System.out.println("Error al guardar el libro: " + e.getMessage());
        }
    }

    private void mostrarLibrosRegistrados() {
        try {
            libros = libroRepository.findAll();
            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados...");
            } else {
                libros.forEach(libro -> System.out.printf("""
                    Libro: %s
                    Autor: %s
                    Idioma: %s
                    Descargas: %s
                    %n""",
                        libro.getTitle(),
                        libro.getAuthor(),
                        libro.getLanguage(),
                        libro.getDownload_count()));
            }
        } catch (Exception e) {
            System.err.println("Ocurrió un error al intentar recuperar los libros: " + e.getMessage());
        } finally {
            System.out.println("Presione Enter para continuar...");
            scanner.nextLine();
        }
    }

    private void mostrarAutoresRegistrados() {
        try {
            autores = autorRepository.findAll();
            if (autores.isEmpty()) {
                System.out.println("No hay autores registrados.");
            } else {
                System.out.println("Autores registrados:");
                autores.forEach(this::imprimirAutor);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener la lista de autores: " + e.getMessage());
        }
        esperarEntrada();
    }

    private void mostrarAutoresPorFecha() {
        System.out.println("Ingrese el año para buscar autores que estaban vivos en ese periodo:");
        System.out.print("> ");
        try {
            int anio = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            List<Autor> autoresVivos = autorRepository.findAuthorsAliveInYear(anio);

            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + anio + ".");
            } else {
                System.out.println("Autores vivos en el año " + anio + ":");
                autoresVivos.forEach(this::imprimirAutor);
            }
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Por favor, ingrese un año válido.");
            scanner.nextLine(); // Limpiar el buffer en caso de error
        } catch (Exception e) {
            System.err.println("Error al consultar los autores: " + e.getMessage());
        }
        esperarEntrada();
    }

    private void imprimirAutor(Autor autor) {
        System.out.printf("""
        Autor: %s
        Nacimiento: %s
        Fallecimiento: %s
        %n""",
                autor.getName(),
                autor.getBirth_day() != null ? autor.getBirth_day() : "Desconocido",
                autor.getDeath_day() != null ? autor.getDeath_day() : "Aún vivo");
    }

    private void esperarEntrada() {
        System.out.println("Presione Enter para continuar...");
        scanner.nextLine();
    }
//
    private void mostrarPorIdiomas() {
        System.out.println("""
            Seleccione un idioma para mostrar los libros registrados:
            1: Inglés (en)
            2: Español (es)
            3: Francés (fr)
            4: Alemán (de)
            5: Italiano (it)
            6: Otro idioma
            """);
        System.out.print("> ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer

        String idioma = null;

        // Mapear la opción seleccionada a un código de idioma
        switch (opcion) {
            case 1 -> idioma = "en";
            case 2 -> idioma = "es";
            case 3 -> idioma = "fr";
            case 4 -> idioma = "de";
            case 5 -> idioma = "it";
            case 6 -> {
                System.out.println("Ingrese el código del idioma (ISO 639-1, por ejemplo: en, es, fr):");
                System.out.print("> ");
                idioma = scanner.nextLine();
            }
            default -> {
                System.out.println("Opción inválida. Volviendo al menú principal...");
                return;
            }
        }

        try {
            // Consultar libros por idioma en la base de datos
            List<Libro> librosPorIdioma = libroRepository.findByLanguage(idioma);

            // Mostrar resultados
            if (librosPorIdioma.isEmpty()) {
                System.out.println("No se encontraron libros registrados en el idioma seleccionado (" + idioma + ").");
            } else {
                System.out.println("Libros disponibles en el idioma seleccionado (" + idioma + "):");
                librosPorIdioma.forEach(libro -> System.out.printf("""
                    Título: %s
                    Autor: %s
                    Idioma: %s
                    Descargas: %d
                    %n""",
                        libro.getTitle(),
                        libro.getAuthor(),
                        libro.getLanguage(),
                        libro.getDownload_count()));
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al consultar los libros: " + e.getMessage());
        }
    }

    private void mostrarEstadisticasLibros() {
        try {
            // Obtener todos los libros de la base de datos
            List<Libro> libros = libroRepository.findAll();

            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados para generar estadísticas.");
                return;
            }

            // Generar estadísticas con streams
            DoubleSummaryStatistics stats = libros.stream()
                    .mapToDouble(Libro::getDownload_count)
                    .summaryStatistics();

            // Mostrar estadísticas
            System.out.println("Estadísticas de descargas de libros:");
            System.out.printf("Número total de descargas: %.0f%n", stats.getSum());
            System.out.printf("Promedio de descargas: %.2f%n", stats.getAverage());
            System.out.printf("Máximo número de descargas: %.0f%n", stats.getMax());
            System.out.printf("Mínimo número de descargas: %.0f%n", stats.getMin());
        } catch (Exception e) {
            System.out.println("Ocurrió un error al generar las estadísticas: " + e.getMessage());
        }
    }

    private void mostrarTopLibrosMasDescargados() {
        try {
            // Consultar los 10 libros más descargados
            List<Libro> topLibros = libroRepository.findTop10ByOrderByDownloadCountDesc(PageRequest.of(0, 10));

            if (topLibros.isEmpty()) {
                System.out.println("No hay libros registrados.");
                return;
            }

            // Mostrar el top 10
            System.out.println("Top 10 libros más descargados:");
            topLibros.forEach(libro -> System.out.printf("""
                Título: %s
                Autor: %s
                Descargas: %d
                %n""",
                    libro.getTitle(),
                    libro.getAuthor(),
                    libro.getDownload_count()));
        } catch (Exception e) {
            System.out.println("Ocurrió un error al obtener el top 10 de libros: " + e.getMessage());
        }
    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingrese el nombre del autor a buscar:");
        System.out.print("> ");
        String nombreAutor = scanner.nextLine();

        try {
            // Consultar autores por nombre en la base de datos
            List<Autor> autores = autorRepository.findByName(nombreAutor);

            if (autores.isEmpty()) {
                System.out.println("El autor no se encuentra en la base de datos. Buscando en la API...");

                // Consultar la API
                String urlFinal = url + "?search=" + URLEncoder.encode(nombreAutor, StandardCharsets.UTF_8);
                var json = consumoAPI.obtenerDatos(urlFinal);

                // Procesar el JSON y buscar autores
                var rootNode = conversor.obtenerDatos(json, Map.class);
                var results = (List<Map<String, Object>>) rootNode.get("results");

                if (results == null || results.isEmpty()) {
                    System.out.println("No se encontraron autores en la API con el nombre: " + nombreAutor);
                    return;
                }

                // Extraer los datos de los autores y almacenarlos en la base de datos
                for (var result : results) {
                    var datosAutorJson = conversor.obtenerDatos(new ObjectMapper().writeValueAsString(result.get("authors")), DatosAutor[].class);

                    for (DatosAutor datosAutor : datosAutorJson) {
                        Autor autor = new Autor(datosAutor);

                        if (!autorRepository.existsByName(autor.getName())) {
                            autorRepository.save(autor);
                            System.out.println("Autor guardado correctamente: " + autor.getName());
                        } else {
                            System.out.println("El autor ya existe en la base de datos: " + autor.getName());
                        }
                    }
                }

                // Volver a consultar en la base de datos
                autores = autorRepository.findByName(nombreAutor);
            }

            // Mostrar los autores encontrados
            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores con el nombre: " + nombreAutor);
            } else {
                System.out.println("Autores encontrados:");
                autores.forEach(a -> System.out.printf("""
                    Nombre: %s
                    Año de nacimiento: %s
                    Año de fallecimiento: %s
                    %n""",
                        a.getName(),
                        a.getBirth_day() != null ? a.getBirth_day() : "Desconocido",
                        a.getDeath_day() != null ? a.getDeath_day() : "Aún vivo"));
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al buscar el autor: " + e.getMessage());
        }
    }

    private void listarAutoresPorAtributo() {
        System.out.println("""
        ¿Qué atributo deseas consultar?
        1: Año de nacimiento
        2: Año de fallecimiento
        """);
        System.out.print("> ");

        try {
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            if (opcion != 1 && opcion != 2) {
                System.out.println("Opción inválida. Por favor, elige una opción válida.");
                return;
            }

            System.out.println("Ingrese el año a consultar:");
            System.out.print("> ");
            int anio = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            List<Autor> autores = obtenerAutoresPorAtributo(opcion, anio);

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores para el año especificado.");
            } else {
                System.out.println("Autores encontrados:");
                autores.forEach(this::imprimirAutor);
            }
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Por favor, asegúrate de ingresar números.");
            scanner.nextLine(); // Limpiar el buffer en caso de error
        } catch (Exception e) {
            System.err.println("Ocurrió un error al consultar los autores: " + e.getMessage());
        }
    }

    private List<Autor> obtenerAutoresPorAtributo(int opcion, int anio) {
        try {
            return switch (opcion) {
                case 1 -> autorRepository.findByBirthYear(anio);
                case 2 -> autorRepository.findByDeathYear(anio);
                default -> throw new IllegalArgumentException("Opción inválida.");
            };
        } catch (Exception e) {
            System.err.println("Error al acceder al repositorio: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
