package com.checkmarx.integrations.datastore.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scm_repos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "name"})})
public class ScmRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private ScmOrg scmOrg;

    private String name;

    @Column(name = "webhook_id")
    private String webhookId;

    @Column(name = "is_webhook_configured")
    private boolean isWebhookConfigured;
}