package com.workify.auth.Controller.community;

import com.workify.auth.models.community.Community;
import com.workify.auth.repository.community.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    @Autowired
    private CommunityRepository communityRepository;

    @PostMapping
    public ResponseEntity<Community> createCommunity(@RequestBody Community community) {
        Community savedCommunity = communityRepository.save(community);
        return ResponseEntity.ok(savedCommunity);
    }

    @GetMapping
    public ResponseEntity<List<Community>> getAllCommunities() {
        List<Community> communities = communityRepository.findAll();
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<Community> getCommunityDetails(@PathVariable Long communityId) {
        return communityRepository.findById(communityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity<String> joinCommunity(@PathVariable Long communityId, @RequestBody String userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (community.getMembers().contains(userId)) {
            return ResponseEntity.badRequest().body("User is already a member of this community.");
        }

        community.getMembers().add(userId);
        communityRepository.save(community);
        return ResponseEntity.ok("User joined the community successfully.");
    }


    @PostMapping("/{communityId}/leave")
    public ResponseEntity<String> leaveCommunity(@PathVariable Long communityId, @RequestBody String userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (!community.getMembers().contains(userId)) {
            return ResponseEntity.badRequest().body("User is not a member of this community.");
        }

        community.getMembers().remove(userId);
        communityRepository.save(community);
        return ResponseEntity.ok("User left the community successfully.");
    }

}

