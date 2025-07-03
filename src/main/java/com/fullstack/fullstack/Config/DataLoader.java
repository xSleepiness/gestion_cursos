package com.fullstack.fullstack.Config;

import com.fullstack.fullstack.Model.Alumno;
import com.fullstack.fullstack.Model.Curso;
import com.fullstack.fullstack.Repository.AlumnoRepository;
import com.fullstack.fullstack.Repository.CursoRepository;
import com.fullstack.fullstack.Service.UserApiService;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile({"dev", "test"}) // Solo ejecutar en desarrollo y testing
public class DataLoader implements CommandLineRunner {

    @Autowired
    private AlumnoRepository alumnoRepository;
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private UserApiService userApiService;
    
    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        // Si la API externa está disponible, no cargamos datos locales de alumnos
        boolean apiExternaDisponible = userApiService.isApiAvailable();
        
        if (!apiExternaDisponible && alumnoRepository.count() == 0 && cursoRepository.count() == 0) {
            System.out.println("API externa no disponible, cargando datos locales de prueba...");
            loadData();
        } else if (apiExternaDisponible && cursoRepository.count() == 0) {
            System.out.println("API externa disponible, cargando solo datos de cursos...");
            loadCursos();
        } else {
            System.out.println("Datos ya cargados o API externa disponible.");
        }
    }

    private void loadData() {
        System.out.println("Cargando datos de prueba con DataFaker...");
        
        // Crear cursos
        List<Curso> cursos = createCursos();
        cursoRepository.saveAll(cursos);
        
        // Crear alumnos
        List<Alumno> alumnos = createAlumnos();
        alumnoRepository.saveAll(alumnos);
        
        // Asignar alumnos a cursos aleatoriamente
        assignAlumnosToCursos(alumnos, cursos);
        
        System.out.println("Datos de prueba cargados exitosamente!");
        System.out.println("" + cursos.size() + " cursos creados");
        System.out.println("" + alumnos.size() + " alumnos creados");
    }

    private void loadCursos() {
        System.out.println("Cargando solo datos de cursos (API externa disponible)...");
        
        // Crear cursos
        List<Curso> cursos = createCursos();
        cursoRepository.saveAll(cursos);
        
        System.out.println("Datos de cursos cargados exitosamente!");
        System.out.println("" + cursos.size() + " cursos creados");
    }

    private List<Curso> createCursos() {
        List<Curso> cursos = new ArrayList<>();
        
        String[] materias = {
            "Programación Java", "Desarrollo Web", "Base de Datos", 
            "Algoritmos", "Estructura de Datos", "Desarrollo Móvil",
            "Machine Learning", "Ciberseguridad", "DevOps", "Cloud Computing"
        };
        
        for (String materia : materias) {
            Curso curso = new Curso();
            curso.setNombre(materia);
            // Generar descripción más corta (máximo 200 caracteres)
            String descripcion = faker.lorem().sentence(10, 15);
            if (descripcion.length() > 200) {
                descripcion = descripcion.substring(0, 200) + "...";
            }
            curso.setDescripcion(descripcion);
            curso.setDuracion(faker.number().numberBetween(20, 120));
            cursos.add(curso);
        }
        
        return cursos;
    }

    private List<Alumno> createAlumnos() {
        List<Alumno> alumnos = new ArrayList<>();
        
        for (int i = 0; i < 25; i++) {
            Alumno alumno = new Alumno();
            alumno.setNombre(faker.name().fullName());
            alumno.setEmail(faker.internet().emailAddress());
            alumnos.add(alumno);
        }
        
        return alumnos;
    }

    private void assignAlumnosToCursos(List<Alumno> alumnos, List<Curso> cursos) {
        for (Alumno alumno : alumnos) {
            // Cada alumno se inscribe en 1-4 cursos aleatoriamente
            int numCursos = random.nextInt(4) + 1;
            List<Curso> cursosAsignados = new ArrayList<>();
            
            for (int i = 0; i < numCursos; i++) {
                Curso cursoAleatorio = cursos.get(random.nextInt(cursos.size()));
                if (!cursosAsignados.contains(cursoAleatorio)) {
                    alumno.addCurso(cursoAleatorio);
                    cursosAsignados.add(cursoAleatorio);
                }
            }
            
            alumnoRepository.save(alumno);
        }
        
        cursoRepository.saveAll(cursos);
    }
}