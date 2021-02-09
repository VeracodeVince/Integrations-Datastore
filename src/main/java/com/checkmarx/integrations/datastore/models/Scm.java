package com.checkmarx.integrations.datastore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

import static com.checkmarx.integrations.datastore.utils.DBConsts.MAX_LENGTH;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scms", uniqueConstraints = {
        @UniqueConstraint(columnNames = "auth_base_url"),
        @UniqueConstraint(columnNames = "api_base_url"),
        @UniqueConstraint(columnNames = "repo_base_url")})
public class Scm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "scm", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ScmOrg> scmOrgList;

    @Column(name = "auth_base_url")
    private String authBaseUrl;

    @Column(name = "api_base_url")
    private String apiBaseUrl;

    @Column(name = "repo_base_url")
    private String repoBaseUrl;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret", length = MAX_LENGTH)
    private String clientSecret;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_id")
    private ScmType type;
}