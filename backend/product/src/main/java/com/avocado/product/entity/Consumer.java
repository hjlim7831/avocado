package com.avocado.product.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Consumer {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    // 퍼스널컬러 ID, Cascade 설정 X
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_color_id")
    private PersonalColor personalColor;

    // MBTI ID, Cascade 설정 X
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_id")
    private Mbti mbti;

    // 사용자명
    private String name;

    // 프로필 사진
    private String pictureUrl;

    // 나이 & 성별
    @Column(columnDefinition = "CHAR(1)")
    private String gender;
    private Short age;

    @Builder
    public Consumer(UUID id, String name, String pictureUrl) {
        this.id = id;
        this.name = name;
        this.pictureUrl = pictureUrl;
    }

    public void update(PersonalColor personalColor, Mbti mbti, String gender, Short age) {
        this.personalColor = personalColor;
        this.mbti = mbti;
        this.gender = gender;
        this.age = age;
    }
}
