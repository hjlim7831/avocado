import styled from "@emotion/styled";
import { Button, Stack } from "@mui/material";
import { InlineText } from "../../components/atoms";
import { useEffect } from "react";
import {
  ProductCardsRow,
  UserProfile,
  UserStateSummary,
} from "../../components/oranisms";
import { useRouter } from "next/router";
import { ChartPersonalColor } from "../../components/oranisms/charts";
import Link from "next/link";
import Head from "next/head";
import { AppState, useAppSelector, wrapper } from "../../features/store";
import { authenticateTokenInPages } from "../../utils/authenticateTokenInPages";
import { removeTokenAll } from "@/src/utils/tokenManager";
import { useDispatch } from "react-redux";
import { clearAuth } from "@/src/features/auth/authSlice";
import { useGetSnapshotCntAndLikeCntQuery } from "@/src/features/snapshot/snapshotApi";
import {
  useGetCartQuery,
  useGetWishlistQuery,
} from "@/src/features/product/productApi";

const Mypage = () => {
  const member = useAppSelector((state: AppState) => state.auth.member);
  const dispatch = useDispatch();
  const router = useRouter();

  //좋아요 갯수, 스넵샷 게시물 개수
  const { data: userSummary } = useGetSnapshotCntAndLikeCntQuery();

  //장바구니 목록
  const { data: cartList, isLoading } = useGetCartQuery();

  const { data: wishList } = useGetWishlistQuery();

  //로그인 안했다면 로그인 페이지로 보내버리기
  useEffect(() => {
    if (!member) {
      router.push("/login");
    }
  }, []);

  //로그아웃 핸들러
  const handleLogout = () => {
    console.log("로그아웃 버튼이 클릭되었습니다.");
    removeTokenAll();
    dispatch(clearAuth());
    //로그인 페이지로 이동
    router.replace("/");
  };

  // 통계 자료
  const statisticData = useAppSelector(
    (state: AppState) => state.statistic.selectedProductStatisticData
  );
  const ageGenderData = statisticData.age_gender_score;
  const mbtiData = statisticData.mbti_score;
  const personalColorData = statisticData.personal_color_score;

  return (
    <Background>
      <Head>
        <title>마이페이지</title>
      </Head>
      <Stack direction={"column"} spacing={2}>
        {member && <UserProfile member={member} />}
        <Button
          onClick={handleLogout}
          fullWidth
          variant="outlined"
          color="error"
          style={{ padding: "10px" }}
        >
          로그아웃
        </Button>
        <UserStateSummary
          data={{
            cart_cnt: cartList ? cartList.length : 0,
            wishlist_cnt: wishList ? wishList.length : 0,
            snapshot_cnt: userSummary ? userSummary.styleshot_cnt : 0,
            like_cnt: userSummary ? userSummary.like_cnt : 0,
          }}
        />
        <ChartPersonalColor personalColorData={personalColorData} />

        <Box>
          <Stack direction={"row"} justifyContent={"space-between"}>
            <InlineText>최근 본 상품</InlineText>
          </Stack>
          {/* <ProductCardsRow isLogin={!!member} /> */}
        </Box>

        <Box>
          <Stack direction={"row"} justifyContent={"space-between"}>
            <InlineText>구매 내역</InlineText>
            <Link href="/user/orderList">
              <InlineText>더보기 +</InlineText>
            </Link>
          </Stack>
          {/* <ProductCardsRow isLogin={!!member} /> */}
        </Box>
      </Stack>
    </Background>
  );
};

export default Mypage;

const Background = styled.div`
  padding: 10px 10px 70px 10px;
`;

const Box = styled.div`
  padding-top: 100px;
`;

export const getServerSideProps = wrapper.getServerSideProps(
  (store) => async (context) => {
    // 쿠키의 토큰을 통해 로그인 확인, 토큰 리프레시, 실패 시 로그아웃 처리 등
    await authenticateTokenInPages(
      { req: context.req, res: context.res },
      store
    );

    return {
      props: {},
    };
  }
);
