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
@Table(name = "scms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"base_url"})})
public class Scm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "scm", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ScmOrg> scmOrgList = new ArrayList<>();

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret", length = MAX_LENGTH)
    private String clientSecret;
}