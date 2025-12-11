package com.example.grpc.job;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.util.logging.Logger;

public class JobServiceImpl extends JobServiceGrpc.JobServiceImplBase {
    private static final Logger logger = Logger.getLogger(JobServiceImpl.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    @Override
    public void startJob(JobRequest request, StreamObserver<JobUpdate> responseObserver) {
        String jobId = UUID.randomUUID().toString();
        String description = request.getDescription();
        int durationSeconds = request.getDurationSeconds() > 0 ? request.getDurationSeconds() : 10;
        
        logger.info("Starting job: " + jobId + " - " + description);

        // Initial update
        responseObserver.onNext(JobUpdate.newBuilder()
                .setJobId(jobId)
                .setStatus("PENDING")
                .setProgressPercent(0)
                .setMessage("Job started: " + description)
                .setTimestamp(System.currentTimeMillis())
                .build());

        // Simulate progress
        final int[] progress = {0};
        final int increment = 100 / durationSeconds;

        scheduler.scheduleAtFixedRate(() -> {
            progress[0] += increment;
            if (progress[0] >= 100) {
                progress[0] = 100;
            }

            JobUpdate.Builder update = JobUpdate.newBuilder()
                    .setJobId(jobId)
                    .setTimestamp(System.currentTimeMillis());

            if (progress[0] < 100) {
                update.setStatus("RUNNING")
                      .setProgressPercent(progress[0])
                      .setMessage("Processing... " + progress[0] + "%");
            } else {
                update.setStatus("COMPLETED")
                      .setProgressPercent(100)
                      .setMessage("Job finished successfully.");
            }

            responseObserver.onNext(update.build());

            if (progress[0] >= 100) {
                responseObserver.onCompleted();
                logger.info("Job completed: " + jobId);
                throw new RuntimeException("Stop scheduler"); // Hack to stop this task
            }

        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void watchJobs(Empty request, StreamObserver<JobUpdate> responseObserver) {
        // For simplicity, WatchJobs just stays open and sends a heartbeat or new job notifications
        // In a real app, we would subscribe to a job registry.
        // Here we just keep the stream alive.
        
        scheduler.scheduleAtFixedRate(() -> {
            responseObserver.onNext(JobUpdate.newBuilder()
                    .setJobId("system")
                    .setStatus("HEARTBEAT")
                    .setTimestamp(System.currentTimeMillis())
                    .setMessage("Server is alive")
                    .build());
        }, 0, 5, TimeUnit.SECONDS);
    }
}
