package com.checkmarx.integrations.datastore.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "scm_repos")
public class ScmRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "org_id")
    private ScmOrg scmOrg;

    private String name;

    @Column(name = "is_webhook_configured")
    private boolean isWebhookConfigured;
}