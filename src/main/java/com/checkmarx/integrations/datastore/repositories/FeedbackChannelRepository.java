package com.checkmarx.integrations.datastore.repositories;

import com.checkmarx.integrations.datastore.models.publishing.FeedbackChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface FeedbackChannelRepository extends JpaRepository<FeedbackChannel, Long> {
    @Query(value = "SELECT project.feedbackProfile.channels FROM CxProject project WHERE project.identity = ?1")
    Set<FeedbackChannel> getByProjectIdentity(String projectIdentity);
}
