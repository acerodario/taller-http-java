package com.example.demo;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private List<Map<String, Object>> productos = new ArrayList<>();

    public ProductoController() {
        productos.add(new HashMap<>(Map.of("id", 1, "nombre", "Mouse", "precio", 50)));
        productos.add(new HashMap<>(Map.of("id", 2, "nombre", "Teclado", "precio", 100)));
    }

    // GET: Listar todos los productos
    @GetMapping
    public List<Map<String, Object>> getAll() {
        return productos;
    }

    // POST: Crear un nuevo producto
    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, Object> nuevo) {
        if (!nuevo.containsKey("nombre") || ((String) nuevo.get("nombre")).isBlank()) {
            return Map.of("error", "El nombre del producto es obligatorio");
        }
        if (!nuevo.containsKey("precio") || !(nuevo.get("precio") instanceof Number)) {
            return Map.of("error", "El precio es obligatorio y debe ser numérico");
        }
        double precio = ((Number) nuevo.get("precio")).doubleValue();
        if (precio < 0) {
            return Map.of("error", "El precio no puede ser negativo");
        }

        int nuevoId = productos.size() + 1;
        nuevo.put("id", nuevoId);
        productos.add(new HashMap<>(nuevo));

        return Map.of("mensaje", "Producto creado", "data", nuevo);
    }

    // PUT: Reemplazar completamente un producto
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable int id, @RequestBody Map<String, Object> actualizado) {
        for (Map<String, Object> producto : productos) {
            if (producto.get("id").equals(id)) {
                if (!actualizado.containsKey("nombre") || ((String) actualizado.get("nombre")).isBlank()) {
                    return Map.of("error", "El nombre no puede estar vacío");
                }
                if (!actualizado.containsKey("precio") || !(actualizado.get("precio") instanceof Number)) {
                    return Map.of("error", "El precio debe ser numérico");
                }

                double precio = ((Number) actualizado.get("precio")).doubleValue();
                if (precio < 0) {
                    return Map.of("error", "El precio no puede ser negativo");
                }

                producto.put("nombre", actualizado.get("nombre"));
                producto.put("precio", precio);
                producto.put("id", id);

                return Map.of("mensaje", "Producto actualizado", "data", producto);
            }
        }
        return Map.of("error", "Producto no encontrado");
    }

    // PATCH: Actualizar parcialmente un producto
    @PatchMapping("/{id}")
    public Map<String, Object> patch(@PathVariable int id, @RequestBody Map<String, Object> cambios) {
        for (Map<String, Object> producto : productos) {
            if (producto.get("id").equals(id)) {
                if (cambios.containsKey("nombre")) {
                    String nombre = (String) cambios.get("nombre");
                    if (nombre.isBlank()) {
                        return Map.of("error", "El nombre no puede estar vacío");
                    }
                    producto.put("nombre", nombre);
                }

                if (cambios.containsKey("precio")) {
                    try {
                        double precio = ((Number) cambios.get("precio")).doubleValue();
                        if (precio < 0) {
                            return Map.of("error", "El precio no puede ser negativo");
                        }
                        producto.put("precio", precio);
                    } catch (Exception e) {
                        return Map.of("error", "El precio debe ser numérico");
                    }
                }

                return Map.of("mensaje", "Producto modificado parcialmente", "data", producto);
            }
        }
        return Map.of("error", "Producto no encontrado");
    }

    // DELETE: Eliminar un producto
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable int id) {
        boolean eliminado = productos.removeIf(p -> p.get("id").equals(id));
        if (eliminado) {
            return Map.of("mensaje", "Producto eliminado correctamente");
        } else {
            return Map.of("error", "Producto no encontrado");
        }
    }

    // HEAD: Verificar si el producto existe
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public Map<String, Object> head(@PathVariable int id) {
        boolean existe = productos.stream().anyMatch(p -> p.get("id").equals(id));
        return Map.of("existe", existe);
    }

    // OPTIONS: Mostrar métodos disponibles
    @RequestMapping(method = RequestMethod.OPTIONS)
    public Map<String, Object> options() {
        return Map.of(
                "metodos", List.of("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS", "TRACE"),
                "descripcion", "Métodos soportados para /productos"
        );
    }

    // TRACE: Manejo manual de la solicitud TRACE
    @RequestMapping(value = "/**", method = RequestMethod.TRACE)
    public Map<String, Object> handleTrace(HttpServletRequest request) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("metodo", request.getMethod());
        info.put("uri", request.getRequestURI());

        Map<String, String> headers = new LinkedHashMap<>();
        Collections.list(request.getHeaderNames())
                .forEach(name -> headers.put(name, request.getHeader(name)));

        info.put("headers", headers);
        info.put("descripcion", "Echo del request TRACE (implementación manual)");
        return info;
    }
}
