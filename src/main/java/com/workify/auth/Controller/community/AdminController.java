package com.workify.auth.Controller.community;

import com.workify.auth.repository.community.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/communities")
public class AdminController {

    @Autowired
    private CommunityRepository communityRepository;

    @DeleteMapping("/{communityId}")
    public ResponseEntity<String> deleteCommunity(@PathVariable Long communityId) {
        communityRepository.deleteById(communityId);
        return ResponseEntity.ok("Community deleted successfully.");
    }

    @DeleteMapping("/{communityId}/members/{userId}")
    public ResponseEntity<String> removeUserFromCommunity(@PathVariable Long communityId, @PathVariable String userId) {
        // Logic to remove a user from the community
        return ResponseEntity.ok("User removed from the community.");
    }
}

