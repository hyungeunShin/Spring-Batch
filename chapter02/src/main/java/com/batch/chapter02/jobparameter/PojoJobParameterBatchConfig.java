package com.batch.chapter02.jobparameter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class PojoJobParameterBatchConfig {
    @Bean
    public Job pojoJobParameterJob(JobRepository jobRepository, Step pojoJobParameterStep) {
        return new JobBuilder("pojoJobParameterJob", jobRepository)
                .start(pojoJobParameterStep)
                .build();
    }

    @Bean
    public Step pojoJobParameterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet pojoJobParameterTasklet) {
        return new StepBuilder("pojoJobParameterStep", jobRepository)
                .tasklet(pojoJobParameterTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet pojoJobParameterTasklet(PojoJobParameter parameter) {
        return (contribution, chunkContext) -> {
            log.info("name: {}", parameter.getName());
            log.info("age: {}", parameter.getAge());
            log.info("id: {}", parameter.getId());
            return RepeatStatus.FINISHED;
        };
    }

    @Slf4j
    @Getter
    @Setter
    @StepScope
    @Component
    public static class PojoJobParameter {
        //필드 주입
        @Value("#{jobParameters['name']}")
        private String name;

        private int age;

        private final String id;

        //생성자 파라미터 주입
        public PojoJobParameter(@Value("#{jobParameters['id']}") String id) {
            log.info("생성자 파라미터 주입");
            this.id = id;
        }

        //setter 주입
        @Value("#{jobParameters['age']}")
        public void setAge(int age) {
            log.info("setter 주입");
            this.age = age;
        }
    }
}
