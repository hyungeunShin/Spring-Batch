package com.batch.chapter02.jobparameter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class JobParameterValidatorBatchConfig {
    /*
    JobParametersValidator를 통해 잡 실행 전에 파라미터를 검증할 수 있다.
    그러나 파라미터 검증을 위해 매번 JobParametersValidator를 직접 구현하는 건 너무 번거로운 일이다.
    다행히 Spring Batch는 이미 DefaultJobParametersValidator라는 기본 구현체를 제공한다.
    단순히 파라미터의 존재 여부만 확인하면 될 때는 이걸 사용하면 된다.
    */

    @Bean
    public Job jobParameterValidatorJob1(JobRepository jobRepository, Step jobParameterValidatorStep, TestJobParameterValidator validator) {
        return new JobBuilder("jobParameterValidatorJob1", jobRepository)
                .validator(validator)
                .start(jobParameterValidatorStep)
                .build();
    }

    @Bean
    public Job jobParameterValidatorJob2(JobRepository jobRepository, Step jobParameterValidatorStep) {
        return new JobBuilder("jobParameterValidatorJob2", jobRepository)
                //첫 번째 배열에는 반드시 있어야 하는 파라미터들을, 두 번째 배열에는 있어도 되고 없어도 되는 파라미터들을 지정
                .validator(new DefaultJobParametersValidator(
                        new String[] {"a"}, //필수
                        new String[] {"b"}  //선택
                ))
                .start(jobParameterValidatorStep)
                .build();
    }

    @Bean
    public Step jobParameterValidatorStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet jobParameterValidatorTasklet) {
        return new StepBuilder("jobParameterValidatorStep", jobRepository)
                .tasklet(jobParameterValidatorTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet jobParameterValidatorTasklet() {
        return (contribution, chunkContext) -> {
            JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
            Long a = jobParameters.getLong("a");
            Long b = jobParameters.getLong("b");

            log.info("a: {}", a);
            log.info("b: {}", b);

            return RepeatStatus.FINISHED;
        };
    }
}
