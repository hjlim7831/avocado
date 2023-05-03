import styled from "@emotion/styled";
import { useRouter } from "next/router";
import { Stack } from "@mui/material";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import Chip from "@mui/material/Chip";
import Image from "next/image";
import Rating from "@mui/material/Rating";

import { useState } from "react";

import { Pie, Doughnut } from "react-chartjs-2";
import { Bubble } from "react-chartjs-2";
import { IconButton } from "@mui/material";
import BookmarkBorderOutlinedIcon from "@mui/icons-material/BookmarkBorderOutlined";
import BookmarkOutlinedIcon from "@mui/icons-material/BookmarkOutlined";
import { BlockText, InlineText } from "../components/atoms";
import { ProductDetailImage, ProductCardsRow } from "../components/oranisms";
import { ChartPersonalColor } from "../components/oranisms/charts";

import FormControl from "@mui/material";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import SendIcon from "@mui/icons-material/Send";
import { useRef } from "react";

const ProductDescription = (props: any) => {
  const [rating, setRating] = useState(2);
  const inputRef = useRef<HTMLInputElement>();

  function submitHandler(e: any) {
    e.preventDefault();
    if (inputRef.current) {
      //여기 useMutation 작성해야함
      console.log({
        rating,
        content: inputRef.current.value,
      });
    }
  }
  return (
    <Background>
      <form onSubmit={submitHandler}>
        <Grid container alignItems={"center"}>
          <Grid item xs={9}>
            <TextField
              fullWidth
              id="standard-basic"
              label="리뷰작성"
              variant="outlined"
              inputRef={inputRef}
            />
          </Grid>
          <Grid item xs={3}>
            <Rating
              size="small"
              name="simple-controlled"
              value={rating}
              onChange={(event, newValue) => {
                setRating(newValue ? newValue : 0);
              }}
              style={{
                border: "1px solid #dddddd",
                boxSizing: "border-box",
                padding: "18px 10px",
                borderRadius: "5px",
              }}
            />
          </Grid>
        </Grid>
      </form>
    </Background>
  );
};

//여기는 SeoulNamsan 적용 안되서 기본 sens-self로 함
const Background = styled.div``;

export default ProductDescription;
