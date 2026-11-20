package com.mpaz.backend.controller;

import com.mpaz.backend.model.ObjetivoAprendizaje;
import com.mpaz.backend.repository.ObjetivoAprendizajeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objetivos")
@CrossOrigin(origins = "*")
public class ObjetivoAprendizajeController {

    private final ObjetivoAprendizajeRepository repository;

    public ObjetivoAprendizajeController(ObjetivoAprendizajeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<ObjetivoAprendizaje>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoAprendizaje> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ObjetivoAprendizaje> create(@RequestBody ObjetivoAprendizaje oa) {
        if (oa.getStatus() == null || oa.getStatus().isBlank()) {
            oa.setStatus("PUBLICADO");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(oa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoAprendizaje> update(@PathVariable Long id, @RequestBody ObjetivoAprendizaje detalles) {
        return repository.findById(id).map(existente -> {
            existente.setSubject(detalles.getSubject());
            existente.setOaCode(detalles.getOaCode());
            existente.setOaTitle(detalles.getOaTitle());
            existente.setUnit(detalles.getUnit());
            existente.setDescription(detalles.getDescription());
            existente.setStatus(detalles.getStatus());
            return ResponseEntity.ok(repository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}