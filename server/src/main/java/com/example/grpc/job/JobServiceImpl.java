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

    // Track active jobs to cancel them properly
    private final ConcurrentHashMap<String, java.util.concurrent.ScheduledFuture<?>> activeJobs = new ConcurrentHashMap<>();

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

        // We need a wrapper to hold the future so we can access it inside the lambda (or just look it up in the map)
        
        Runnable jobTask = new Runnable() {
            @Override
            public void run() {
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
                    
                    // Send update
                    try {
                        responseObserver.onNext(update.build());
                    } catch (Exception e) {
                        logger.warning("Error sending update for job " + jobId + ": " + e.getMessage());
                        cancelJob(jobId);
                    }
                } else {
                    update.setStatus("COMPLETED")
                          .setProgressPercent(100)
                          .setMessage("Job finished successfully.");
                    
                    try {
                        responseObserver.onNext(update.build());
                        responseObserver.onCompleted();
                    } catch (Exception e) {
                        logger.warning("Error completing job " + jobId + ": " + e.getMessage());
                    } finally {
                        logger.info("Job completed: " + jobId);
                        cancelJob(jobId);
                    }
                }
            }
        };

        java.util.concurrent.ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(jobTask, 1, 1, TimeUnit.SECONDS);
        activeJobs.put(jobId, future);
    }

    private void cancelJob(String jobId) {
        java.util.concurrent.ScheduledFuture<?> future = activeJobs.remove(jobId);
        if (future != null) {
            future.cancel(false);
        }
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
