package com.checkmarx.integrations.datastore.models;

import lombok.*;

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
public class ScanDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "scan_id")
    private String scanId;

    private LocalDateTime created;

    @Column(length = 10000)
    private String body;

    @PrePersist
    public void setCreatedTimestamp() {
        if (created == null) {
            // Make sure the timestamps in the storage are UTC.
            created = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime();
        }
    }
}
