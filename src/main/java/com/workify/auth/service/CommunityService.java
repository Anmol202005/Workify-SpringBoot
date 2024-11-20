package com.workify.auth.service;

import com.workify.auth.models.User;
import com.workify.auth.models.community.Community;
import com.workify.auth.models.dto.CommunityDto;
import com.workify.auth.repository.UserRepository;
import com.workify.auth.repository.community.CommunityRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public CommunityService(CommunityRepository communityRepository, UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }


    public void createCommunity(CommunityDto community,HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        Community community1 = new Community();
        community1.setName(community.getName());
        community1.setDescription(community.getDescription());
        community1.setCreatedBy(user.get());
        community1.setCreationDateTime(LocalDateTime.now());
        communityRepository.save(community1);
    }

    public List<Community> getAllCommunity() {
        return communityRepository.findAll();
    }
    public Community getCommunityById(Long id) {
        if(communityRepository.existsById(id)) {
        return communityRepository.findById(id).get();}
        else{
            throw new RuntimeException("Community not found");
        }
    }
    @Transactional
    public void deleteCommunityById(Long id) {
        if(communityRepository.existsById(id)) {
            communityRepository.deleteById(id);}
        else{
            throw new RuntimeException("Community not found");
        }

    }
    public void updateCommunity(Long communityId, CommunityDto community, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        if(communityRepository.existsById(communityId)) {
        Community community1 = communityRepository.findById(communityId).get();
        if(!community1.getCreatedBy().equals(user.get())){
            throw new RuntimeException("unauthorized");
        }
        community1.setName(community.getName());
        community1.setDescription(community.getDescription());
        community1.setUpdateDateTime(LocalDateTime.now());}
    }
    public ResponseEntity<String> joinCommunity(Long communityId, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (community.getMembers().contains(user.get().getId())) {
            return ResponseEntity.badRequest().body("User is already a member of this community.");
        }

        community.getMembers().add(user.get().getId());
        communityRepository.save(community);
        return ResponseEntity.ok("User joined the community successfully.");
    }


    public ResponseEntity<String> leaveCommunity(Long communityId, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (!community.getMembers().contains(user.get().getId())) {
            return ResponseEntity.badRequest().body("User is not a member of this community.");
        }

        community.getMembers().remove(user.get().getId());
        communityRepository.save(community);
        return ResponseEntity.ok("User left the community successfully.");
    }
}
