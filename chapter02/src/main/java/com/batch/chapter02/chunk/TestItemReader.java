package com.batch.chapter02.chunk;

import org.springframework.batch.item.ItemReader;

public class TestItemReader implements ItemReader<Integer> {
    private int a = 1;
    private final int b = 10;

    @Override
    public Integer read() {
        if(a > b) {
            //ItemReader가 null을 반환하는 것이 청크 지향 처리 Step의 종료 시점
            return null;
        }

        return a++;
    }
}
