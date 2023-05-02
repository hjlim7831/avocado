package com.avocado.product.repository;

import com.avocado.product.config.OrderByNull;
import com.avocado.product.dto.query.QScoreDTO;
import com.avocado.product.dto.query.ScoreDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.avocado.product.entity.QAgeGenderScore.ageGenderScore;
import static com.avocado.product.entity.QMbti.mbti;
import static com.avocado.product.entity.QMbtiScore.mbtiScore;
import static com.avocado.product.entity.QMerchandise.merchandise;
import static com.avocado.product.entity.QPersonalColor.personalColor;
import static com.avocado.product.entity.QPersonalColorScore.personalColorScore;

@Repository
@RequiredArgsConstructor
public class ScoreRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 상품들의 대표 퍼스널컬러를 한 번에 조회하는 쿼리
     * @param merchandiseIds : 상품 ID 목록
     * @return : 대표 퍼스널컬러
     */
    public List<ScoreDTO> findPersonalColors(List<Long> merchandiseIds) {
        return queryFactory
                .select(new QScoreDTO(
                        merchandise.id,
                        personalColor.kind,
                        personalColorScore.score.sum()
                ))
                .from(personalColorScore)
                .join(personalColorScore.personalColor, personalColor)
                .join(personalColorScore.merchandise, merchandise)
                .where(
                        inMerchandiseIds(merchandiseIds)
                )
                .groupBy(
                        merchandise.id,
                        personalColor.kind
                )
                .orderBy(
                        OrderByNull.DEFAULT
                )
                .fetch();
    }
    // 단건 조회
    public String findPersonalColor(Long merchandiseId) {
        return queryFactory
                .select(
                        personalColor.kind
                )
                .from(personalColorScore)
                .join(personalColorScore.personalColor, personalColor)
                .join(personalColorScore.merchandise, merchandise)
                .where(
                        eqMerchandiseId(merchandiseId)
                )
                .groupBy(
                        merchandise.id,
                        personalColor.kind
                )
                .orderBy(
                        personalColorScore.score.sum().desc()
                )
                .fetchFirst();
    }

    /**
     * 특정 상품의 대표 MBTI를 조회하는 쿼리
     * @param merchandiseIds : 상품 ID 목록
     * @return : 대표 MBTI
     */
    public List<ScoreDTO> findMbtis(List<Long> merchandiseIds) {
        return queryFactory
                .select(new QScoreDTO(
                        merchandise.id,
                        mbti.kind,
                        mbtiScore.score.sum()
                ))
                .from(mbtiScore)
                .join(mbtiScore.mbti, mbti)
                .join(mbtiScore.merchandise, merchandise)
                .where(
                        inMerchandiseIds(merchandiseIds)
                )
                .groupBy(
                        merchandise.id,
                        mbti.kind
                )
                .orderBy(
                        OrderByNull.DEFAULT
                )
                .fetch();
    }
    // 단건 조회
    public String findMbti(Long merchandiseId) {
        return queryFactory
                .select(mbti.kind)
                .from(mbtiScore)
                .join(mbtiScore.mbti, mbti)
                .join(mbtiScore.merchandise, merchandise)
                .where(
                        eqMerchandiseId(merchandiseId)
                )
                .groupBy(
                        merchandise.id,
                        mbti.kind
                )
                .orderBy(
                        mbtiScore.score.sum().desc()
                )
                .fetchFirst();
    }

    /**
     * 특정 상품의 대표 나이대를 조회하는 쿼리
     * @param merchandiseIds : 상품 ID 목록
     * @return : 대표 MBTI
     */
    public List<ScoreDTO> findAges(List<Long> merchandiseIds) {
        return queryFactory
                .select(new QScoreDTO(
                        merchandise.id,
                        ageGenderScore.age.stringValue(),
                        ageGenderScore.score.sum()
                ))
                .from(ageGenderScore)
                .join(ageGenderScore.merchandise, merchandise)
                .where(
                        inMerchandiseIds(merchandiseIds)
                )
                .groupBy(
                        merchandise.id,
                        ageGenderScore.age
                )
                .orderBy(
                        OrderByNull.DEFAULT
                )
                .fetch();
    }
    // 단건 조회
    public String findAge(Long merchandiseId) {
        return queryFactory
                .select(
                        ageGenderScore.age.stringValue()
                )
                .from(ageGenderScore)
                .join(ageGenderScore.merchandise, merchandise)
                .where(
                        eqMerchandiseId(merchandiseId)
                )
                .groupBy(
                        merchandise.id,
                        ageGenderScore.age
                )
                .orderBy(
                        ageGenderScore.score.sum().desc()
                )
                .fetchFirst();
    }

    // 상품 ID 조건
    private BooleanExpression inMerchandiseIds(List<Long> merchandiseIds) {
        return merchandiseIds != null ? merchandise.id.in(merchandiseIds) : null;
    }
    private BooleanExpression eqMerchandiseId(Long merchandiseId) {
        return merchandiseId != null ? merchandise.id.eq(merchandiseId) : null;
    }
}