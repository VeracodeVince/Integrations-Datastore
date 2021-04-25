package com.checkmarx.integrations.datastore.models;

import lombok.*;

import javax.persistence.*;

import static com.checkmarx.integrations.datastore.utils.DBConsts.MAX_LENGTH;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_identity", length = MAX_LENGTH, unique = true)
    private String tenantIdentity;
}