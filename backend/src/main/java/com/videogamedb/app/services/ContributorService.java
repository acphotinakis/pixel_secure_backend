// ContributorService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.Contributor;
import com.videogamedb.app.repositories.ContributorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContributorService {
    private final ContributorRepository contributorRepository;

    public ContributorService(ContributorRepository contributorRepository) {
        this.contributorRepository = contributorRepository;
    }

    public List<Contributor> getAllContributors() {
        return contributorRepository.findAll();
    }

    public Optional<Contributor> getContributorById(String id) {
        return contributorRepository.findById(id);
    }

    public List<Contributor> getContributorsByType(String type) {
        return contributorRepository.findByType(type);
    }

    public List<Contributor> searchContributorsByName(String name) {
        return contributorRepository.findByContributorNameContainingIgnoreCase(name);
    }

    public Contributor createContributor(Contributor contributor) {
        return contributorRepository.save(contributor);
    }

    public Contributor updateContributor(String id, Contributor contributorDetails) {
        Optional<Contributor> optionalContributor = contributorRepository.findById(id);
        if (optionalContributor.isPresent()) {
            Contributor contributor = optionalContributor.get();
            contributor.setContributorName(contributorDetails.getContributorName());
            contributor.setType(contributorDetails.getType());
            return contributorRepository.save(contributor);
        }
        return null;
    }

    public void deleteContributor(String id) {
        contributorRepository.deleteById(id);
    }
}