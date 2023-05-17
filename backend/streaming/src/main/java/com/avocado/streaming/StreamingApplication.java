package com.avocado.streaming;

import com.avocado.Merchandise;
import com.avocado.PurchaseHistory;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

import com.avocado.Result;
import com.avocado.ActionType;

import org.apache.kafka.streams.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.Duration;
import java.util.*;

@SpringBootApplication
public class StreamingApplication {
	private static String applicationId = "streaming-v2";
	private static String bootstrapServer = "a62e5b172168c40419f9a1af18763a94-214776296.ap-northeast-2.elb.amazonaws.com:9094";
	private static Integer numStreamThreads = 1;
	private static Integer commitInterval = 10*1000;

	public static void main(String[] args) throws IOException {
		System.setProperty("spring.devtools.restart.enabled", "false"); // restart / launcher 클래스 로더 이슈 없애줌
		SpringApplication.run(StreamingApplication.class, args);



		final Serde<String> stringSerde = Serdes.String();
		final Serde<Long> longSerde = Serdes.Long();

		boolean isKeySerde = false;

		final Serde<GenericRecord> genericAvroSerde = new GenericAvroSerde();
 		genericAvroSerde.configure(Collections.singletonMap(
				 "schema.registry.url", "http://a5ef82e13fcbc44689f93c4924981608-494875664.ap-northeast-2.elb.amazonaws.com:8081"), isKeySerde);

		 final Serde<Result> resultSerde = new SpecificAvroSerde<>();
		 resultSerde.configure(Collections.singletonMap(
				 "schema.registry.url", "http://a5ef82e13fcbc44689f93c4924981608-494875664.ap-northeast-2.elb.amazonaws.com:8081"), isKeySerde);


		 final Serde<PurchaseHistory> purchaseHistorySerde = new SpecificAvroSerde<>();
		purchaseHistorySerde.configure(Collections.singletonMap(
				"schema.registry.url", "http://a5ef82e13fcbc44689f93c4924981608-494875664.ap-northeast-2.elb.amazonaws.com:8081"), isKeySerde);


		Properties streamsConfiguration = new Properties();
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);

		streamsConfiguration.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, numStreamThreads);
//		streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, commitInterval);

		streamsConfiguration.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://a5ef82e13fcbc44689f93c4924981608-494875664.ap-northeast-2.elb.amazonaws.com:8081");
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, longSerde.getClass());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
//		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, resultSerde.getClass());

		StreamsBuilder builder = new StreamsBuilder();






		//////////// payment counter
		KStream<String, PurchaseHistory> purchaseHistoryStream = builder.stream("test-purchase-history1", Consumed.with(stringSerde, purchaseHistorySerde));
		KStream<Long, Result> paymentStream = purchaseHistoryStream
//				.flatMapValues((ValueMapper<PurchaseHistory, Iterable<Merchandise>>) PurchaseHistory::getMerchandises);
				.flatMap((key, value) -> {
					List<KeyValue<Long, Result>> result = new ArrayList<>();
					for (Merchandise merchandise : value.getMerchandises()) {
						for (int i=0; i< merchandise.getQuantity(); i++) {
							result.add(KeyValue.pair(merchandise.getMerchandiseId(), Result.newBuilder()
									.setUserId(value.getUserId())
									.setAction(ActionType.PAYMENT)
									.build()));
						}
					}
					return result;
				});
//				.map((key, value) -> {
//					Result result = Result.newBuilder()
//							.setUserId(key)
//							.setAction(ActionType.PAYMENT)
//							.build();
//					return new KeyValue<>(key, result);
//				});
		paymentStream.foreach((key, value) -> System.out.println("purchase result stream >> key: " + key + ", value: " + value));
		paymentStream.to("test-result1", Produced.with(longSerde, resultSerde));

		//////////// normal view counter
		KStream<Long, GenericRecord> viewStream = builder.stream("test-view", Consumed.with(longSerde, genericAvroSerde));
		KStream<Long, Result> viewResult = viewStream.map((key, value) -> KeyValue.pair(key, Result.newBuilder()
				.setUserId(value.get("userId").toString())
				.setAction(ActionType.VIEW)
				.build()));
		viewResult.foreach((key, value) -> System.out.println("View Stream >> key: " + key + ", value: " + value));
		viewResult.to("test-result1", Produced.with(longSerde, resultSerde));

		//////////// click counter
		KStream<Long, GenericRecord> clickStream = builder.stream("test-click1", Consumed.with(longSerde, genericAvroSerde));
		KStream<Long, Result> resultStream = clickStream.map(((key, value) -> KeyValue.pair(key, Result.newBuilder()
				.setUserId(value.get("userId").toString())
				.setAction(ActionType.CLICK)
				.build())));
		resultStream.foreach((key, value) -> System.out.println("Click Stream >> key: " + key + ", value: " + value));
		resultStream.to("test-result1", Produced.with(longSerde, resultSerde));

		//////////// cart counter
		KStream<Long, GenericRecord> cartStream = builder.stream("test-cart", Consumed.with(longSerde, genericAvroSerde));
		KStream<Long, Result> cartResult = cartStream.map(((key, value) -> KeyValue.pair(key, Result.newBuilder()
				.setUserId(value.get("userId").toString())
				.setAction(ActionType.CART)
				.build())));
		cartResult.foreach((key, value) -> System.out.println("Cart Stream >> key: " + key + ", value: " + value));
		cartResult.to("test-result1", Produced.with(longSerde, resultSerde));

//		KGroupedStream<Long, GenericRecord> clickGroup = clickStream.groupByKey(Grouped.with(longSerde, genericAvroSerde));

//		KTable<Windowed<Long>, Long> clickTable = clickGroup
//				//				.windowedBy(TimeWindows.ofSizeAndGrace())
//				.windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMillis(5000)))
//				.count();
//		clickTable.toStream().foreach((key, value) -> System.out.println("Click Table - Key: " + key + ", Value: " + value));


		// adview counter
		KStream<Long, GenericRecord> adviewStream = builder.stream("test-ad-view1", Consumed.with(longSerde, genericAvroSerde));
		adviewStream.foreach((key, value) -> System.out.println("Adview Stream - Key: " + key + ", Value: " + value));


//		KGroupedStream<Long, GenericRecord> adviewGroup = adviewStream.groupByKey(Grouped.with(longSerde, genericAvroSerde));
////		clickGroup.foreach((key, value) -> System.out.println("Grouped Stream - Key: " + key + ", Value: " + value));
//
//		KTable<Windowed<Long>, Long> adviewTable = adviewGroup
////								.windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMillis(5000)))
//				.windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMillis(5000)))
//				.count();
//		adviewTable.toStream().foreach((key, value) -> System.out.println("Adview Table - Key: " + key + ", Value: " + value));

		///////////// inner join click & adview
		// define join windows
		JoinWindows adClickJoinWindows = JoinWindows
				.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(30))  // 30 seconds tolerance
				.after(Duration.ofSeconds(30)) // click event must happen within 15 seconds after adview event
				.before(Duration.ZERO);  // click event must happen after adview event
//
//		KStream<Long, String> joined = adviewStream
//				.join(
//						clickStream,
//						(adviewValue, clickValue) -> {
////							// cast to GenericRecord and read the userId field
////							GenericRecord adviewRecord = (GenericRecord) adviewValue;
////							GenericRecord clickRecord = (GenericRecord) clickValue;
//							String adviewUserId = adviewValue.get("userId").toString();
//							String clickUserId = clickValue.get("userId").toString();
//							return adviewUserId + "/" + clickUserId;
//						},
//						adClickJoinWindows
//				)
//				.filter(
//						(key, value) -> {
//							String[] parts = value.split("/");
////							System.out.println("  join with key: " + key + ", left val: " + parts[0] + ", right val: " + parts[1]);
//							return parts[0].equals(parts[1]);  // only keep events where user id is the same
//						}
//				);
//
//		KStream<Long, Result> ad_click_stream = joined.map(((key, value) -> KeyValue.pair(key, Result.newBuilder()
//				.setUserId(value.split("/")[0])
//				.setAction(ActionType.AD_CLICK)
//				.build())));
//		ad_click_stream.foreach((k, v) -> System.out.println("ad click >> key: " + k + ", value: " + v));
//		ad_click_stream.to("test-result1", Produced.with(Serdes.Long(), resultSerde));


		//////////// ad click + payment join
		// define join windows
		JoinWindows adPaymentJoinWindows = JoinWindows
				.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(3*60))  // 30 seconds tolerance
				.after(Duration.ofSeconds(3*60)) // click event must happen within 15 seconds after adview event
				.before(Duration.ZERO);  // click event must happen after adview event

//		KStream<Long, String> ad_payment_joined = ad_click_stream
//				.join(
//						paymentStream,
//						(adclickResult, paymentResult) -> adclickResult.getUserId() + "/" + paymentResult.getUserId(),
//						adPaymentJoinWindows
//				)
//				.filter(
//						(key, value) -> {
//							String[] parts = value.split("/");
//							return parts[0].equals(parts[1]);  // only keep events where user id is the same
//						}
//				);
//		KStream<Long, Result> ad_payment_stream = ad_payment_joined.map((key, value) -> KeyValue.pair(key, Result.newBuilder()
//						.setUserId(value.split("/")[0])
//						.setAction(ActionType.AD_PAYMENT)
//						.build()));
//		ad_payment_stream.foreach((k, v) -> System.out.println("ad payment >> key: " + k + ", value: " + v));

		Topology topology = builder.build();
		KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);
		streams.start();
//		streams.close();
	}

}