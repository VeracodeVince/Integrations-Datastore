package com.checkmarx.integrations.datastore.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scm_types", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})})
public class ScmType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column
    private String scope;

    @OneToMany(mappedBy = "type", cascade = CascadeType.REMOVE)
    private List<Scm> scms;
}
