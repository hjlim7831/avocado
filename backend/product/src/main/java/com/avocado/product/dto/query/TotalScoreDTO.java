package com.avocado.product.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TotalScoreDTO {
    private final Long merchandiseId;
    private final Short ageGroup;
    private final Long score;

    @QueryProjection
    public TotalScoreDTO(Long merchandiseId, Short ageGroup, Long score) {
        this.merchandiseId = merchandiseId;
        this.ageGroup = ageGroup;
        this.score = score;
    }
}
