package com.checkmarx.integrations.datastore.models.publishing;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "feedback_profiles")
public class FeedbackProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String name;

    @ManyToMany
    @JoinTable(name = "channel_profile_mapping",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id"))
    Set<FeedbackChannel> channels;
}
