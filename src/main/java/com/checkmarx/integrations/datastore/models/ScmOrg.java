package com.checkmarx.integrations.datastore.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "scm_orgs")
public class ScmOrg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "scm_id")
    private Scm scm;

    private String name;

    @Column(name = "cx_flow_url")
    private String cxFlowUrl;

    @Column(name = "cx_flow_config")
    private String cxFlowConfig;

    @Column(name = "cx_go_token")
    private String cxGoToken;

    private String team;
}