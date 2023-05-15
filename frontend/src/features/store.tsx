import { configureStore, ThunkAction } from "@reduxjs/toolkit";
import { Action } from "redux";
import { createWrapper } from "next-redux-wrapper";
import { combineReducers } from "@reduxjs/toolkit";
import { exampleSlice } from "./example/exampleSlice";
import { examplePostsApi } from "../queries/examplePostApi";
import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux";
import thunkMiddleware from "redux-thunk";
import { testProductListApi } from "../queries/testProductListApi";
import { authSlice } from "./auth/authSlice";
import { productSlice } from "./product/productSlice";
import { authApi } from "./auth/authApi";
import { productApi } from "./product/productApi";
import { searchApi } from "../queries/searchApi";
import { memberInfoApi } from "./memberInfo/memberInfoApi";
import { paymentApi } from "./payment/paymentApi";
import { paymentSlice } from "./payment/paymentSlice";

// Root reducer 설정
const rootReducer = combineReducers({
  example: exampleSlice.reducer,
  auth: authSlice.reducer,
  product: productSlice.reducer,
  payment: paymentSlice.reducer,
  [examplePostsApi.reducerPath]: examplePostsApi.reducer,
  [testProductListApi.reducerPath]: testProductListApi.reducer,
  [authApi.reducerPath]: authApi.reducer,
  [productApi.reducerPath]: productApi.reducer,
  [paymentApi.reducerPath]: paymentApi.reducer,
  [searchApi.reducerPath]: searchApi.reducer, //검색 reducer
  [memberInfoApi.reducerPath]: memberInfoApi.reducer, //유저정보 수정 api
});

// https://github.com/kirill-konshin/next-redux-wrapper#redux-toolkit
// next.js + next-redux-wrapper + RTK
const makeStore = () => {
  return configureStore({
    reducer: rootReducer,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware().concat(
        thunkMiddleware,
        examplePostsApi.middleware,
        testProductListApi.middleware,
        authApi.middleware,
        productApi.middleware,
        paymentApi.middleware,
        searchApi.middleware, //검색 api 미들웨어
        memberInfoApi.middleware // 유저정보 수정 api
      ),
    devTools: true,
  });
};

export type AppStore = ReturnType<typeof makeStore>;
export type AppState = ReturnType<AppStore["getState"]>;
export type AppDispatch = AppStore["dispatch"];
export type AppThunk<ReturnType = void> = ThunkAction<
  ReturnType,
  AppState,
  unknown,
  Action
>;

// Use throughout your app instead of plain `useDispatch` and `useSelector`
export const useAppDispatch = () => useDispatch<AppDispatch>();
export const useAppSelector: TypedUseSelectorHook<AppState> = useSelector;

// wrapper
export const wrapper = createWrapper<AppStore>(makeStore);
