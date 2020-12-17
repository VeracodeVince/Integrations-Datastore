package com.checkmarx.integrations.datastore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.checkmarx.integrations.datastore.utils.DBConsts.MAX_LENGTH;

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
    private List<ScmRepo> scmRepoList = new ArrayList<>();

    @OneToMany(mappedBy = "scmOrg", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Token> tokenList = new ArrayList<>();

    @Column(name = "org_identity")
    private String orgIdentity;

    @Column(name = "org_name")
    private String orgName;

    @Column(name = "cx_flow_url")
    private String cxFlowUrl;

    @Column(name = "cx_flow_config")
    private String cxFlowConfig;

    @Column(name = "cx_go_token", length = MAX_LENGTH)
    private String cxGoToken;

    private String team;
}