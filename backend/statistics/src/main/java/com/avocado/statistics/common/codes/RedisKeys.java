package com.avocado.statistics.common.codes;

import org.springframework.stereotype.Component;

@Component
public class RedisKeys {

    public final String merchandiseIdSet = "st-mc-ids";

    // Common Prefix
    public final String commPrefix = "st-";

    // Advertise Prefix
    public final String adPrefix = "ad-";

    // Recommend Prefix
    public final String recPrefix = "rec-";

    // Category Type
    public final String ageGenderPrefix = "ag-";
    public final String mbtiPrefix = "mbti-";
    public final String personalColorPrefix = "pc-";

    // Result Type
    public final String viewPrefix = "view:";
    public final String clickPrefix = "click:";
    public final String likePrefix = "like:";
    public final String cartPrefix = "cart:";
    public final String paymentPrefix = "pay:";

    // Statistics Type
    public final String providerPrefix = "provider";
}
