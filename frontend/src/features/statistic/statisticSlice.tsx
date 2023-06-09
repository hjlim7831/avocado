import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { HYDRATE } from "next-redux-wrapper";

// 상품 상세
export interface StatisticDataForProductDetail {
  age_gender_score: number[]; // 홀수 번째가 여자, 짝수 번째가 남자. 10대부터 70대 이상까지.
  mbti_score: number[]; // mbti_id 인덱스 순서대로 값 넘어감
  personal_color_score: number[]; // personal_color_id 인덱스 순서대로 값 넘어감
}

// 메인 페이지 추천
export interface StatisticDataForPersonalRecommendation {
  consumer_recommends: RecommendProductItem[];
  personal_color_recommends: RecommendProductItem[];
  mbti_recommends: RecommendProductItem[];
}

export interface RecommendProductItem {
  brand_name: string;
  merchandise_id: number;
  merchandise_category: string;
  merchandise_name: string;
  price: number;
  discounted_price: number;
  mbti?: string;
  personal_color?: string;
  age_group?: string;
  image_url: string;
  is_wishlist: boolean;
}

// 판매자 통계
export interface StatisticDataForProvider {
  click_count: number;
  sell_count: number;
  total_revenue: number;
  merchandise_count: number;
  genders: GendersData[];
  mbtis: MbtisData[];
  personal_colors: PersonalColorsData[];
  age_groups: AgeGroupsData[];
}

export interface StatisticDataForContumer {
  total_money: number;
  categories: CategoriesData[];
}

export interface GendersData {
  gender: string;
  count: number;
}

export interface MbtisData {
  kind: string;
  count: number;
}

export interface PersonalColorsData {
  kind: string;
  count: number;
}

export interface AgeGroupsData {
  age_group: number;
  count: number;
}

export interface CategoriesData {
  category: string;
  count: number;
}

interface StatisticState {
  selectedProductStatisticData: StatisticDataForProductDetail;
  recommendedProductsData: StatisticDataForPersonalRecommendation;
  consumerStatisticData: StatisticDataForContumer;
  providerStatisticData: StatisticDataForProvider;
}

const initialState: StatisticState = {
  selectedProductStatisticData: {
    age_gender_score: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    mbti_score: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    personal_color_score: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  },
  recommendedProductsData: {
    consumer_recommends: [],
    personal_color_recommends: [],
    mbti_recommends: [],
  },
  consumerStatisticData: {
    total_money: 0,
    categories: [],
  },
  providerStatisticData: {
    click_count: 0,
    sell_count: 0,
    total_revenue: 0,
    merchandise_count: 0,
    genders: [],
    mbtis: [],
    personal_colors: [],
    age_groups: [],
  },
};

export const statisticSlice = createSlice({
  name: "statistic",
  initialState,
  reducers: {
    setSelectedProductStatisticData: (
      state,
      action: PayloadAction<StatisticDataForProductDetail>
    ) => {
      state.selectedProductStatisticData = action.payload;
    },

    setRecommendProductsData: (
      state,
      action: PayloadAction<StatisticDataForPersonalRecommendation>
    ) => {
      state.recommendedProductsData = action.payload;
    },

    setConsumerStatisticData: (
      state,
      action: PayloadAction<StatisticDataForContumer>
    ) => {
      state.consumerStatisticData = action.payload;
    },
    setProviderStatisticData: (
      state,
      action: PayloadAction<StatisticDataForProvider>
    ) => {
      state.providerStatisticData = action.payload;
    },
  },
  extraReducers: {
    [HYDRATE]: (state, action) => {
      // console.log("HYDRATE-STATISTIC", state, action.payload);
      return {
        ...state,
        ...action.payload.statistic,
      };
    },
  },
});

export const {
  setSelectedProductStatisticData,
  setRecommendProductsData,
  setConsumerStatisticData,
  setProviderStatisticData,
} = statisticSlice.actions;
