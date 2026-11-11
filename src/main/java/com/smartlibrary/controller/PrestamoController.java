package com.smartlibrary.controller;

import com.smartlibrary.dto.PrestamoRequest;
import com.smartlibrary.dto.PrestamoResponse;
import com.smartlibrary.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Capa de presentación: traduce HTTP ↔ PrestamoService. No contiene lógica de negocio.
 */
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    public ResponseEntity<PrestamoResponse> registrarPrestamo(@Valid @RequestBody PrestamoRequest request) {
        PrestamoResponse respuesta = prestamoService.registrarPrestamo(request);
        URI ubicacion = URI.create("/api/prestamos/" + respuesta.id());
        return ResponseEntity.created(ubicacion).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<PrestamoResponse>> listarPrestamos() {
        return ResponseEntity.ok(prestamoService.listarPrestamos());
    }
}
