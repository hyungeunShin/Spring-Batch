package com.batch.chapter02.chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class TestItemWriter implements ItemWriter<String> {
    @Override
    public void write(Chunk<? extends String> chunk) {
        log.info("새로운 청크: {}", chunk.size());
        chunk.forEach(c -> log.info("{}", c));
    }
}
