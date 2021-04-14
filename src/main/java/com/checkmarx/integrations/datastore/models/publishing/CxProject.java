package com.checkmarx.integrations.datastore.models.publishing;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cx_projects",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"identity"})
        })
public class CxProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "feedback_profile_id")
    private FeedbackProfile feedbackProfile;
}
