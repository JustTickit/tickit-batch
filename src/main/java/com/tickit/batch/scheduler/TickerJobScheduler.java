package com.tickit.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TickerJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job tickerJob;
    private final JobExplorer jobExplorer;

    @Scheduled(fixedDelay = 1000)
    public void runTickerJob() {
        if (isJobRunning("tickerJob")) {
            log.debug("[Scheduler] tickerJob 이미 실행 중입니다. 스킵합니다.");
            return;
        }

        try {
            log.info("[Scheduler] TickerJob 실행 시작");

            JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

            jobLauncher.run(tickerJob, jobParameters);

            log.info("[Scheduler] TickerJob 실행 완료");
        } catch (Exception e) {
            log.error("[Scheduler] TickerJob 실행 실패: ", e);
        }
    }

    private boolean isJobRunning(String jobName) {
        return !jobExplorer.findRunningJobExecutions(jobName).isEmpty();
    }
}