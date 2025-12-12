import { Component, signal, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JobServiceClient } from './proto/JobServiceClientPb';
import { JobRequest, JobUpdate } from './proto/job_pb';
import * as grpcWeb from 'grpc-web';

interface JobLog {
  jobId: string;
  status: string;
  progress: number;
  message: string;
  timestamp: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'gRPC Job Monitor';

  // Active jobs list (tracked by ID)
  jobs = signal<JobLog[]>([]);

  // Client
  private client: JobServiceClient;

  constructor(private ngZone: NgZone) {
    // Connect to Envoy
    this.client = new JobServiceClient('http://localhost:8080');
  }

  startSimulation() {
    // Start 3 parallel jobs
    for (let i = 1; i <= 3; i++) {
        this.startSingleJob(i);
    }
  }

  private startSingleJob(index: number) {
    const request = new JobRequest();
    // Random duration between 300 (5m) and 1200 (20m) seconds.
    const duration = Math.floor(Math.random() * (1200 - 300 + 1)) + 300; 
    
    request.setDescription(`Simulation Job #${index} (${(duration / 60).toFixed(1)} mins)`);
    request.setDurationSeconds(duration);

    const stream = this.client.startJob(request, {});

    stream.on('data', (response: JobUpdate) => {
        const jobId = response.getJobId();
        const newUpdate: JobLog = {
            jobId: jobId,
            status: response.getStatus(),
            progress: response.getProgressPercent(),
            message: response.getMessage(),
            timestamp: response.getTimestamp()
        };

        this.ngZone.run(() => {
            this.jobs.update(currentJobs => {
                const existingIndex = currentJobs.findIndex(j => j.jobId === jobId);
                if (existingIndex !== -1) {
                    // Update existing job in place
                    const updated = [...currentJobs];
                    updated[existingIndex] = newUpdate;
                    return updated;
                } else {
                    // Add new job
                    return [...currentJobs, newUpdate];
                }
            });
        });
    });

    stream.on('error', (err: grpcWeb.RpcError) => {
        console.error('Stream error:', err);
    });
  }
}

