package com.batch.chapter02.jobparameter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
class JobParameterBuilderBatchConfigTest {
    @Autowired
    private JobLauncher launcher;

    @Autowired
    private Job jobParameterBuilderJob;

    @Test
    void jobManualRun_test() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("a", "aaa")
                .addLong("b", 111L)
                .toJobParameters();

        JobExecution jobExecution = launcher.run(jobParameterBuilderJob, jobParameters);

        Assertions.assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }
}