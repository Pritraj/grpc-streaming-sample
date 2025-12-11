import { Component, signal, WritableSignal } from '@angular/core';
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

  // Signals
  jobLogs: WritableSignal<JobLog[]> = signal([]);
  isStreaming = signal(false);

  // Client
  private client: JobServiceClient;

  constructor() {
    // Connect to Envoy
    this.client = new JobServiceClient('http://localhost:8080');
  }

  startJob() {
    console.log('Starting job...');
    const request = new JobRequest();
    request.setDescription('Sample Long Running Job');
    request.setDurationSeconds(10); // 10 seconds

    this.isStreaming.set(true);

    const stream = this.client.startJob(request, {});

    stream.on('data', (response: JobUpdate) => {
      const log: JobLog = {
        jobId: response.getJobId(),
        status: response.getStatus(),
        progress: response.getProgressPercent(),
        message: response.getMessage(),
        timestamp: response.getTimestamp(),
      };

      console.log('Received update:', log);

      this.jobLogs.update((logs) => [log, ...logs]);
    });

    stream.on('status', (status: grpcWeb.Status) => {
      console.log('Stream status:', status);
    });

    stream.on('end', () => {
      console.log('Stream ended');
      this.isStreaming.set(false);
    });

    stream.on('error', (err: grpcWeb.RpcError) => {
      console.error('Stream error:', err);
      this.isStreaming.set(false);
    });
  }
}
