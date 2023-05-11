package com.avocado.product.service;

import com.avocado.product.dto.query.CartMerchandiseDTO;
import com.avocado.product.dto.response.CartMerchandiseResp;
import com.avocado.product.entity.Cart;
import com.avocado.product.entity.Consumer;
import com.avocado.product.entity.Merchandise;
import com.avocado.product.exception.AccessDeniedException;
import com.avocado.product.exception.ErrorCode;
import com.avocado.product.exception.InvalidValueException;
import com.avocado.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ConsumerRepository consumerRepository;
    private final MerchandiseRepository merchandiseRepository;

    // 대표 퍼스널컬러, MBTI, 나이대 조회용 service
    private final ScoreService scoreService;

    /**
     * 장바구니 내역 등록
     * @param consumerId : 요청한 소비자의 ID
     * @param merchandiseId : 등록할 상품의 ID
     */
    @Transactional
    public void addProductToCart(UUID consumerId, Long merchandiseId) {
        // 이미 존재하는지 확인
        Cart originCart = cartRepository.findByConsumerIdAndMerchandiseId(consumerId, merchandiseId);
        if (originCart != null)
            throw new InvalidValueException(ErrorCode.EXISTS_CART);

        // 등록할 상품, 사용자 프록시 조회
        Consumer consumer = consumerRepository.getOne(consumerId);
        Merchandise merchandise = merchandiseRepository.getOne(merchandiseId);

        // Entity 생성 및 저장
        Cart cart = Cart.builder()
                .consumer(consumer)
                .merchandise(merchandise)
                .build();
        cartRepository.save(cart);
    }

    /**
     * 장바구니 목록 조회
     * @param consumerId : 요청한 소비자의 ID
     * @return : 해당 소비자의 장바구니 목록
     */
    @Transactional(readOnly = true)
    public List<CartMerchandiseResp> showMyCart(UUID consumerId) {
        // 상품 정보 리스트 조회
        List<CartMerchandiseDTO> myCart = cartRepository.findMyCart(consumerId);
        try {
            return scoreService.insertPersonalInfoIntoList(myCart, CartMerchandiseResp.class);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * 장바구니 내역 삭제
     * @param consumerId : 요청한 소비자의 ID
     * @param cartId : 삭제 요청한 장바구니 ID
     */
    @Transactional
    public void removeProductFromCart(UUID consumerId, Long cartId) {
        // 장바구니 내역 찾기
        Cart cart = cartRepository.findById(cartId);

        // 본인의 장바구니가 아니라면 Forbidden 예외
        if (cart != null && !cart.getConsumer().getId().equals(consumerId))
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);

        // 장바구니가 존재하지 않을 경우 InvalidValueException 반환
        // 본 예외를 먼저 확인하게 되면, 제3자가 불특정 타인의 장바구니 존재 여부를 파악할 수 있음
        if (cart == null)
            throw new InvalidValueException(ErrorCode.NO_CART);

        // 삭제
        cartRepository.delete(cart);
    }
}