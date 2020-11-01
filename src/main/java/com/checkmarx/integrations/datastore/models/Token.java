package com.checkmarx.integrations.datastore.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "type", "token"})})
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "org_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ScmOrg scmOrg;

    private String type;

    @Column(name = "token")
    private String rawToken;
}