package com.workify.auth.Controller.community;

import com.workify.auth.models.community.Community;
import com.workify.auth.models.dto.CommunityDto;
import com.workify.auth.models.dto.ResponseMessage;
import com.workify.auth.repository.community.CommunityRepository;
import com.workify.auth.service.CommunityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private CommunityService communityService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createCommunity(@RequestBody CommunityDto community,HttpServletRequest request) {
        communityService.createCommunity(community,request);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Community created successfully")
                .build());
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Community>> getAllCommunities() {
        List<Community> communities = communityService.getAllCommunity();
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<Community> getCommunityDetails(@PathVariable Long communityId) {

        return ResponseEntity.ok(communityService.getCommunityById(communityId));
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity<String> joinCommunity(@PathVariable Long communityId, HttpServletRequest request) {
       return communityService.joinCommunity(communityId,request);
    }


    @PostMapping("/{communityId}/leave")
    public ResponseEntity<String> leaveCommunity(@PathVariable Long communityId, HttpServletRequest request) {
        return communityService.leaveCommunity(communityId,request);
    }
    @DeleteMapping("/delete/{communityId}")
    public ResponseEntity<String> deleteCommunity(@PathVariable Long communityId) {
        communityService.deleteCommunityById(communityId);
        return ResponseEntity.ok("Community deleted successfully");
    }
    @PatchMapping("/update/{communityId}")
    public ResponseEntity<String> updateCommunity(@PathVariable Long communityId, @RequestBody CommunityDto community,HttpServletRequest request) {
        communityService.updateCommunity(communityId,community,request);
        return ResponseEntity.ok("Community updated successfully");
    }

}

