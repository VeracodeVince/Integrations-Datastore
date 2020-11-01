package com.checkmarx.integrations.datastore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "scm_orgs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"scm_id", "name"})})
public class ScmOrg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scm_id")
    private Scm scm;

    @OneToMany(mappedBy = "scmOrg")
    @JsonIgnore
    private List<ScmRepo> scmRepoList = new ArrayList<>();

    private String name;

    @Column(name = "cx_flow_url")
    private String cxFlowUrl;

    @Column(name = "cx_flow_config")
    private String cxFlowConfig;

    @Column(name = "cx_go_token")
    private String cxGoToken;

    private String team;
}