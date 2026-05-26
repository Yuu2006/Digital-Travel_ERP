package com.digitaltravel.erp.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DichVuTourThucTeId implements Serializable {
    @Column(name = "MaTourThucTe", length = 50)
    String MaTourThucTe;

    @Column(name = "MaDichVuThem", length = 50)
    String MaDichVuThem;
}
