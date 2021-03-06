package com.checkmarx.integrations.datastore.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static com.checkmarx.integrations.datastore.utils.DBConsts.MAX_LENGTH;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scm_repos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "repo_identity"})})
public class ScmRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private ScmOrg scmOrg;

    @Column(name = "repo_identity", length = MAX_LENGTH)
    private String repoIdentity;

    @Column(name = "webhook_id", length = MAX_LENGTH)
    private String webhookId;

    @Column(name = "webhook_key", length = MAX_LENGTH)
    private String webhookKey;

    @Column(name = "is_webhook_configured")
    private boolean isWebhookConfigured;
}