package br.ufma.lsdi.SmartMeterIssuerSSI.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "TB_ROLE")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true, updatable = false, length = 32)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean active;
}
