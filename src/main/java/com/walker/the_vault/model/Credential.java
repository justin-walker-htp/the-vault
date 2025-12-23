package com.walker.the_vault.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credentials")
public class Credential extends BaseEntity {

    @Column(nullable = false)
    private String url; // "netflix.com"

    @Column(nullable = false)
    private String username; // "chill_watcher_99"

    @Column(nullable = false)
    private String encryptedPassword; // Never store this in plain text

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user; // The owner of this secret
}
