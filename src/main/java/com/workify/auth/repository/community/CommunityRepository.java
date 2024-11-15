package com.workify.auth.repository.community;

import com.workify.auth.models.community.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {}
