package com.mpaz.backend.repository;

import com.mpaz.backend.model.ObjetivoAprendizaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjetivoAprendizajeRepository extends JpaRepository<ObjetivoAprendizaje, Long> {
}