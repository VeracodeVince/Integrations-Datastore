package com.checkmarx.integrations.datastore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scm_orgs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"scm_id", "org_identity"})})
public class ScmOrg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scm_id")
    private Scm scm;

    @OneToMany(mappedBy = "scmOrg", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<ScmRepo> repos;

    @Column(name = "org_identity")
    private String orgIdentity;

    @Column(name = "cx_flow_url")
    private String cxFlowUrl;

    @Column(name = "cx_flow_config", length = 10000)
    private String cxFlowConfig;

    @ManyToOne
    @JoinColumn(name = "token_id")
    @JsonIgnore
    private Token accessToken;

    private String team;
}