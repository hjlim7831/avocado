package com.avocado.payment.kafka.service;

import com.avocado.Merchandise;
import com.avocado.PurchaseHistory;
import com.avocado.payment.entity.redis.Purchasing;
import com.avocado.payment.entity.redis.PurchasingMerchandise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KafkaProducer {

    @Value(value = "${spring.kafka.purchase-history-config.topic}")
    private String purchaseHistoryTopic;
    private final KafkaTemplate<String, PurchaseHistory> purchaseHistoryKafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, PurchaseHistory> purchaseHistoryKafkaTemplate) {
        this.purchaseHistoryKafkaTemplate = purchaseHistoryKafkaTemplate;
    }

    public void sendPurchaseHistory(Purchasing purchasing, Map<Long, Integer> inventories, String createdAt) {
        // make merchandises list by purchasing
        List<Merchandise> merchandises = new ArrayList<>();
        for (PurchasingMerchandise pm : purchasing.getMerchandises()) {
            Merchandise merchandise = Merchandise.newBuilder()
                    .setMerchandiseId(pm.getMerchandise_id())
                    .setPrice(pm.getPrice())
                    .setQuantity(pm.getQuantity())
                    .setSize(pm.getSize())
                    .setProviderId(pm.getProvider_id())
                    .setLeftover(inventories.get(pm.getMerchandise_id()) - pm.getQuantity())
                    .build();
            merchandises.add(merchandise);
        }

        // make avro class
        PurchaseHistory purchaseHistory = PurchaseHistory.newBuilder()
                .setUserId(purchasing.getConsumer_id())
                .setTotalPrice(purchasing.getTotal_price())
                .setMerchandises(merchandises)
                .setCreatedAt(createdAt)
                .build();

        ListenableFuture<SendResult<String, PurchaseHistory>> future = purchaseHistoryKafkaTemplate.send(purchaseHistoryTopic, purchasing.getId(), purchaseHistory);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.warn("Unable to send message: [{}] due to : {}", purchaseHistory, ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, PurchaseHistory> result) {
                log.info("purchase history sent: [{}] with partition = [{}] offset=[{}]", purchaseHistory, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });
    }

}
