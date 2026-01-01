package com.example.grpc.job

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class JobServiceImpl : JobServiceGrpcKt.JobServiceCoroutineImplBase() {

    override fun startJob(request: JobRequest): Flow<JobUpdate> = flow {
        val jobId = UUID.randomUUID().toString()
        val description = request.description
        val durationSeconds = if (request.durationSeconds > 0) request.durationSeconds else 10
        
        println("Starting job: $jobId - $description")

        // Initial update
        emit(
            JobUpdate.newBuilder()
                .setJobId(jobId)
                .setStatus("PENDING")
                .setProgressPercent(0)
                .setMessage("Job started: $description")
                .setTimestamp(System.currentTimeMillis())
                .build()
        )

        for (step in 1 until durationSeconds) {
            delay(1000)
            val currentProgress = (step.toDouble() / durationSeconds * 100).toInt()
            
            emit(
                JobUpdate.newBuilder()
                    .setJobId(jobId)
                    .setStatus("RUNNING")
                    .setProgressPercent(currentProgress)
                    .setMessage("Processing... $currentProgress%")
                    .setTimestamp(System.currentTimeMillis())
                    .build()
            )
        }
        
        // Final wait and completion
        delay(1000)
        
        emit(
            JobUpdate.newBuilder()
                .setJobId(jobId)
                .setStatus("COMPLETED")
                .setProgressPercent(100)
                .setMessage("Job finished successfully.")
                .setTimestamp(System.currentTimeMillis())
                .build()
        )
        
        println("Job completed: $jobId")
    }

    override fun watchJobs(request: Empty): Flow<JobUpdate> = flow {
        while (true) {
            emit(
                JobUpdate.newBuilder()
                    .setJobId("system")
                    .setStatus("HEARTBEAT")
                    .setTimestamp(System.currentTimeMillis())
                    .setMessage("Server is alive")
                    .build()
            )
            delay(5000)
        }
    }
}
