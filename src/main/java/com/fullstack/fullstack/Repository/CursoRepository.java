package com.fullstack.fullstack.Repository;

import com.fullstack.fullstack.Model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
