package com.avocado.commercial.Entity;

import com.avocado.commercial.Dto.response.Analysis;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Entity
@Table(name="commercial_statistic")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommercialStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private long id;

    @Column(nullable = false, columnDefinition = "BIGINT")
    private long exposureCnt;

    @Column(nullable = false, columnDefinition = "BIGINT")
    private long clickCnt;

    @Column(nullable = false, columnDefinition = "BIGINT")
    private long purchaseAmount;

    @Column(nullable = false, columnDefinition = "BIGINT")
    private long quantity;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private long commercialId;

    @Column(nullable = false, columnDefinition = "DATE")
    private String date;

    public Analysis toDto(){
        Analysis commercialStatisticRespDto = Analysis.builder()
                .click_cnt(this.clickCnt)
                .purchase_amount(this.purchaseAmount)
                .exposure_cnt(this.exposureCnt)
                .quantity(this.quantity)
                .date(this.date)
                .build();
        return commercialStatisticRespDto;
    }
}
