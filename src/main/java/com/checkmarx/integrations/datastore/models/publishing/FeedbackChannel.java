package com.checkmarx.integrations.datastore.models.publishing;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "feedback_channels")
public class FeedbackChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "plugin_id", nullable = false)
    private String pluginId;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    private JsonNode body;

    @ManyToMany(mappedBy = "channels")
    Set<FeedbackProfile> profiles;
}
