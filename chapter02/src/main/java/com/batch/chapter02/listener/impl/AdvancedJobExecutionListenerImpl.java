package com.batch.chapter02.listener.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
public class AdvancedJobExecutionListenerImpl implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecution.getExecutionContext().put("resultMap", generateMap());
        log.info("AdvancedJobExecutionListenerImpl.beforeJob");
    }

    private Map<String, Object> generateMap() {
        List<String> first = List.of(
                "aaa", "bbb"
        );
        List<String> second = List.of(
                "ccc", "ddd", "eee", "fff"
        );
        List<String> third = List.of(
                "ggg", "hhh", "iii", "jjj"
        );
        List<String> fourth = List.of(
                "kkk", "lll", "mmm", "nnn"
        );

        Random rand = new Random();

        Map<String, Object> map = new HashMap<>();
        map.put("first", first.get(rand.nextInt(first.size())));
        map.put("second", second.get(rand.nextInt(second.size())));
        map.put("third", third.get(rand.nextInt(third.size())));
        map.put("requiredTools", fourth.get(rand.nextInt(fourth.size())));

        return map;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String resultString = jobExecution.getExecutionContext().getString("resultString");
        Map<String, Object> resultMap = (Map<String, Object>) jobExecution.getExecutionContext().get("resultMap");

        log.info("afterJob.resultString: {}", resultString);
        log.info("afterJob.resultMap: {}", resultMap);

        log.info("AdvancedJobExecutionListenerImpl.afterJob");
    }
}
