package com.checkmarx.integrations.datastore.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "scm_tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "type"})})
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private ScmOrg scmOrg;

    private String type;

    @Column(name = "token")
    private String rawToken;
}