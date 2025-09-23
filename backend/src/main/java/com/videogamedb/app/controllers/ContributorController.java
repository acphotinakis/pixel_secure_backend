// ContributorController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.Contributor;
import com.videogamedb.app.services.ContributorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contributors")
public class ContributorController {
    private final ContributorService contributorService;

    public ContributorController(ContributorService contributorService) {
        this.contributorService = contributorService;
    }

    @GetMapping
    public ResponseEntity<List<Contributor>> getAllContributors() {
        List<Contributor> contributors = contributorService.getAllContributors();
        return ResponseEntity.ok(contributors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contributor> getContributorById(@PathVariable String id) {
        Optional<Contributor> contributor = contributorService.getContributorById(id);
        return contributor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Contributor>> getContributorsByType(@PathVariable String type) {
        List<Contributor> contributors = contributorService.getContributorsByType(type);
        return ResponseEntity.ok(contributors);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Contributor>> searchContributorsByName(@RequestParam String name) {
        List<Contributor> contributors = contributorService.searchContributorsByName(name);
        return ResponseEntity.ok(contributors);
    }

    @PostMapping
    public ResponseEntity<Contributor> createContributor(@RequestBody Contributor contributor) {
        Contributor createdContributor = contributorService.createContributor(contributor);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContributor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contributor> updateContributor(
            @PathVariable String id,
            @RequestBody Contributor contributorDetails) {
        Contributor updatedContributor = contributorService.updateContributor(id, contributorDetails);
        if (updatedContributor != null) {
            return ResponseEntity.ok(updatedContributor);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContributor(@PathVariable String id) {
        contributorService.deleteContributor(id);
        return ResponseEntity.noContent().build();
    }
}