package com.workify.auth.repository.community;

import com.workify.auth.models.community.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByCommunityId(Long communityId);
}
