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
@Table(name = "scms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "base_url", "client_id", "client_secret"})})
public class Scm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "scm")
    @JsonIgnore
    private List<ScmOrg> scmOrgList = new ArrayList<>();

    private String name;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;
}