// VideoGameService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.VideoGame;
import com.videogamedb.app.repositories.VideoGameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideoGameService {
    private final VideoGameRepository videoGameRepository;

    public VideoGameService(VideoGameRepository videoGameRepository) {
        this.videoGameRepository = videoGameRepository;
    }

    public List<VideoGame> getAllVideoGames() {
        return videoGameRepository.findAll();
    }

    public Optional<VideoGame> getVideoGameById(String id) {
        return videoGameRepository.findById(id);
    }

    public List<VideoGame> searchVideoGamesByTitle(String title) {
        return videoGameRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<VideoGame> getVideoGamesByEsrb(String esrbRating) {
        return videoGameRepository.findByEsrb(esrbRating);
    }

    public List<VideoGame> getVideoGamesByDeveloperId(String developerId) {
        return videoGameRepository.findByDeveloperId(developerId);
    }

    public List<VideoGame> getVideoGamesByPublisherId(String publisherId) {
        return videoGameRepository.findByPublisherId(publisherId);
    }

    public List<VideoGame> getVideoGamesByGenreId(String genreId) {
        return videoGameRepository.findByGenreId(genreId);
    }

    public List<VideoGame> getVideoGamesByTitleAndEsrb(String title, String esrb) {
        return videoGameRepository.findByTitleAndEsrb(title, esrb);
    }

    public long countVideoGamesByDeveloperId(String developerId) {
        return videoGameRepository.countByDeveloperId(developerId);
    }

    public long countVideoGamesByPublisherId(String publisherId) {
        return videoGameRepository.countByPublisherId(publisherId);
    }

    public VideoGame createVideoGame(VideoGame videoGame) {
        return videoGameRepository.save(videoGame);
    }

    public VideoGame updateVideoGame(String id, VideoGame videoGameDetails) {
        Optional<VideoGame> optionalVideoGame = videoGameRepository.findById(id);
        if (optionalVideoGame.isPresent()) {
            VideoGame videoGame = optionalVideoGame.get();
            videoGame.setTitle(videoGameDetails.getTitle());
            videoGame.setEsrb(videoGameDetails.getEsrb());
            videoGame.setDevelopers(videoGameDetails.getDevelopers());
            videoGame.setPublishers(videoGameDetails.getPublishers());
            videoGame.setGenres(videoGameDetails.getGenres());
            return videoGameRepository.save(videoGame);
        }
        return null;
    }

    public void deleteVideoGame(String id) {
        videoGameRepository.deleteById(id);
    }
}