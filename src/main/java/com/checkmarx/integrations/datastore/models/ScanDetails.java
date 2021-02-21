package com.checkmarx.integrations.datastore.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "scan_details",
        uniqueConstraints = @UniqueConstraint(columnNames = "scan_id"))
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ScanDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "scan_id")
    private String scanId;

    private LocalDateTime created;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    private JsonNode body;

    @PrePersist
    public void setCreatedTimestamp() {
        if (created == null) {
            // Make sure the timestamps in the storage are UTC.
            created = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime();
        }
    }
}
