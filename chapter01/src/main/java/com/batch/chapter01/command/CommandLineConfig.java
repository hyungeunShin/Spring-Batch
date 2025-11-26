package com.batch.chapter01.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Import(BatchConfig.class)
@Profile("!boot")
@RequiredArgsConstructor
public class CommandLineConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private AtomicInteger a = new AtomicInteger(0);
    private final int b = 5;

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .start(step1())
                .next(step2())
                .next(step3())
                .next(step4())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("step1");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("step2");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int terminated = a.incrementAndGet();
                    log.info("step3-{}/{}", terminated, b);
                    if(terminated < b) {
                        return RepeatStatus.CONTINUABLE;
                    } else {
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step4() {
        return new StepBuilder("step4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("step4");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
