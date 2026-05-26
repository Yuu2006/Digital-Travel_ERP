package com.digitaltravel.erp.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "HDX_TOURTHUCTE")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HdxTourThucTe {
    @EmbeddedId
    HdxTourThucTeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("MaTourThucTe")
    @JoinColumn(name = "MaTourThucTe")
    TourThucTe tourThucTe;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("MaHanhDongXanh")
    @JoinColumn(name = "MaHanhDongXanh")
    HanhDongXanh hanhDongXanh;
}
