package com.batch.chapter02.chunk;

import org.springframework.batch.item.ItemProcessor;

public class TestItemProcessor implements ItemProcessor<Integer, String> {
    @Override
    public String process(Integer i) {
        return String.valueOf(i * 2);
    }
}
