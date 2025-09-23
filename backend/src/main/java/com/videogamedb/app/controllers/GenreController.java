// GenreController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.Genre;
import com.videogamedb.app.services.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable String id) {
        Optional<Genre> genre = genreService.getGenreById(id);
        return genre.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Genre>> searchGenresByName(@RequestParam String name) {
        List<Genre> genres = genreService.searchGenresByName(name);
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Genre> getGenreByName(@PathVariable String name) {
        Genre genre = genreService.getGenreByName(name);
        if (genre != null) {
            return ResponseEntity.ok(genre);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) {
        Genre createdGenre = genreService.createGenre(genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(
            @PathVariable String id,
            @RequestBody Genre genreDetails) {
        Genre updatedGenre = genreService.updateGenre(id, genreDetails);
        if (updatedGenre != null) {
            return ResponseEntity.ok(updatedGenre);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable String id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}