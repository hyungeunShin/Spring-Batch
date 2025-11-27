package com.batch.chapter02.jobparameter;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

@Component
public class TestJobParameterValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        assert parameters != null;
        Long a = parameters.getLong("a");
        if(a == null) {
            throw new JobParametersInvalidException("a == null");
        }

        if(a > 9) {
            throw new JobParametersInvalidException("a > 9");
        }
    }
}
