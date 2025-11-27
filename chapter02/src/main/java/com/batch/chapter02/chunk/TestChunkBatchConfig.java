package com.batch.chapter02.chunk;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TestChunkBatchConfig {
    /*
    ItemReader
        ItemReader는 데이터 소스에서 하나씩 데이터를 읽어온다.
        read() 메서드가 호출될 때마다 데이터를 순차적으로 반환하며 청크 크기만큼 데이터를 읽어야 끝난다.
        다시 말해 청크 크기가 10이면 read()가 10번 호출되어 하나의 청크가 생성된다.

    ItemProcessor
        ItemProcessor는 ItemReader가 읽어온 청크의 각 아이템 하나씩을 처리한다.
        process() 메서드는 청크 전체를 입력받지 않는다.
        청크의 각 아이템을 하나씩 받아서 처리한다는 게 포인트다.
        Spring Batch는 청크의 아이템마다 process()를 반복 호출해서 데이터를 변환하거나 필터링한다.
        다시 말해 청크 크기가 10이면 process()가 10번 호출된다.

    ItemWriter
        ItemProcessor의 처리까지 완료되었다면 이제 청크를 실제로 쓸 차례다.
        ItemReader, ItemProcessor가 각각의 아이템을 하나씩 처리하는 것과 달리 ItemWriter는 청크 전체를 한 번에 입력 받는다.
        다시 말해 청크 단위로 데이터를 저장한다.
        이는 write() 메서드의 파라미터 타입이 Chunk<? extends T>인 것만봐도 알 수 있다.

    청크 처리와 트랜잭션
        데이터를 청크 단위로 처리한다는 건 트랜잭션도 청크 단위로 관리된다는 의미다.
        즉, 각각의 청크 단위 반복마다 별도의 트랜잭션이 시작되고 커밋된다.

        청크 단위로 트랜잭션이 시작되고 커밋된다는 것은 매우 중요한 의미를 갖는다.
        대용량 데이터를 처리하는 도중 중간에 스텝이 실패하더라도 이전 청크 반복에서 처리된 데이터는 이미 안전하게 커밋되어 있어 그만큼의 작업은 보존된다.
        실패한 청크의 데이터만 롤백되므로, 전체 데이터를 처음부터 다시 처리할 필요가 없다.
    */
    private final JobRepository jobRepository;

    @Bean
    public Job testChunkJob() {
        return new JobBuilder("testChunkJob", jobRepository)
                .start(testChunkStep())
                .build();
    }

    @Bean
    public Step testChunkStep() {
        return new StepBuilder("testChunkStep", jobRepository)
                .<Integer, String>chunk(3, new ResourcelessTransactionManager())
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public TestItemReader itemReader() {
        return new TestItemReader();
    }

    @Bean
    public TestItemProcessor itemProcessor() {
        return new TestItemProcessor();
    }

    @Bean
    public TestItemWriter itemWriter() {
        return new TestItemWriter();
    }
}
