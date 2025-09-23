// VideoGameController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.VideoGame;
import com.videogamedb.app.services.VideoGameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/video-games")
public class VideoGameController {
    private final VideoGameService videoGameService;

    public VideoGameController(VideoGameService videoGameService) {
        this.videoGameService = videoGameService;
    }

    @GetMapping
    public ResponseEntity<List<VideoGame>> getAllVideoGames() {
        List<VideoGame> videoGames = videoGameService.getAllVideoGames();
        return ResponseEntity.ok(videoGames);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoGame> getVideoGameById(@PathVariable String id) {
        Optional<VideoGame> videoGame = videoGameService.getVideoGameById(id);
        return videoGame.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<VideoGame>> searchVideoGamesByTitle(@RequestParam String title) {
        List<VideoGame> videoGames = videoGameService.searchVideoGamesByTitle(title);
        return ResponseEntity.ok(videoGames);
    }

    @GetMapping("/esrb/{esrbRating}")
    public ResponseEntity<List<VideoGame>> getVideoGamesByEsrb(@PathVariable String esrbRating) {
        List<VideoGame> videoGames = videoGameService.getVideoGamesByEsrb(esrbRating);
        return ResponseEntity.ok(videoGames);
    }

    @GetMapping("/developer/{developerId}")
    public ResponseEntity<List<VideoGame>> getVideoGamesByDeveloperId(@PathVariable String developerId) {
        List<VideoGame> videoGames = videoGameService.getVideoGamesByDeveloperId(developerId);
        return ResponseEntity.ok(videoGames);
    }

    @GetMapping("/publisher/{publisherId}")
    public ResponseEntity<List<VideoGame>> getVideoGamesByPublisherId(@PathVariable String publisherId) {
        List<VideoGame> videoGames = videoGameService.getVideoGamesByPublisherId(publisherId);
        return ResponseEntity.ok(videoGames);
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<VideoGame>> getVideoGamesByGenreId(@PathVariable String genreId) {
        List<VideoGame> videoGames = videoGameService.getVideoGamesByGenreId(genreId);
        return ResponseEntity.ok(videoGames);
    }

    @GetMapping("/title/{title}/esrb/{esrb}")
    public ResponseEntity<List<VideoGame>> getVideoGamesByTitleAndEsrb(
            @PathVariable String title,
            @PathVariable String esrb) {
        List<VideoGame> videoGames = videoGameService.getVideoGamesByTitleAndEsrb(title, esrb);
        return ResponseEntity.ok(videoGames);
    }

    @GetMapping("/developer/{developerId}/count")
    public ResponseEntity<Long> countVideoGamesByDeveloperId(@PathVariable String developerId) {
        long count = videoGameService.countVideoGamesByDeveloperId(developerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/publisher/{publisherId}/count")
    public ResponseEntity<Long> countVideoGamesByPublisherId(@PathVariable String publisherId) {
        long count = videoGameService.countVideoGamesByPublisherId(publisherId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<VideoGame> createVideoGame(@RequestBody VideoGame videoGame) {
        VideoGame createdVideoGame = videoGameService.createVideoGame(videoGame);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVideoGame);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoGame> updateVideoGame(
            @PathVariable String id,
            @RequestBody VideoGame videoGameDetails) {
        VideoGame updatedVideoGame = videoGameService.updateVideoGame(id, videoGameDetails);
        if (updatedVideoGame != null) {
            return ResponseEntity.ok(updatedVideoGame);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideoGame(@PathVariable String id) {
        videoGameService.deleteVideoGame(id);
        return ResponseEntity.noContent().build();
    }
}