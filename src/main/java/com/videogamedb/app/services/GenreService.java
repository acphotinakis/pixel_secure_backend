// GenreService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.Genre;
import com.videogamedb.app.repositories.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Optional<Genre> getGenreById(String id) {
        return genreRepository.findById(id);
    }

    public List<Genre> searchGenresByName(String name) {
        return genreRepository.findByGenreNameContainingIgnoreCase(name);
    }

    public Genre getGenreByName(String name) {
        return genreRepository.findByGenreName(name);
    }

    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public Genre updateGenre(String id, Genre genreDetails) {
        Optional<Genre> optionalGenre = genreRepository.findById(id);
        if (optionalGenre.isPresent()) {
            Genre genre = optionalGenre.get();
            genre.setGenreName(genreDetails.getGenreName());
            return genreRepository.save(genre);
        }
        return null;
    }

    public void deleteGenre(String id) {
        genreRepository.deleteById(id);
    }
}