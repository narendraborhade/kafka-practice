package com.exampl.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Named;

import java.util.Arrays;
import java.util.Properties;

public class StreamStarterApp {

    public static void main(String[] args) {

        //System.out.println("Hello, World!");

        Properties config = new Properties();
        config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "word-count");
        config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.setProperty(StreamsConfig.AUTOMATIC_CONFIG_PROVIDERS_PROPERTY, "earliest");
        config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();
        //1 - stream from kafka
        KStream<String,String> wordCountInput = builder.stream("word-count-input");
        //2 - map values to lowercase
        KTable<String, Long> wordCounts = wordCountInput.mapValues(value -> value.toLowerCase())
        //3 - flatmap values split by space
        .flatMapValues(value -> Arrays.asList(value.split(" ")))
        //4 - select key to apply a key
        .selectKey((ignoredKey, word) -> word)
        //5 - group by key before aggregation
        .groupByKey()
        //6 - count occurences
         .count(Named.as("Counts"));

        wordCounts.toStream().to("word-count-output");

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();

        System.out.println(streams);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
