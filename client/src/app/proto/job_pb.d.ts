import * as jspb from 'google-protobuf'



export class JobRequest extends jspb.Message {
  getDescription(): string;
  setDescription(value: string): JobRequest;

  getDurationSeconds(): number;
  setDurationSeconds(value: number): JobRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): JobRequest.AsObject;
  static toObject(includeInstance: boolean, msg: JobRequest): JobRequest.AsObject;
  static serializeBinaryToWriter(message: JobRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): JobRequest;
  static deserializeBinaryFromReader(message: JobRequest, reader: jspb.BinaryReader): JobRequest;
}

export namespace JobRequest {
  export type AsObject = {
    description: string,
    durationSeconds: number,
  }
}

export class JobUpdate extends jspb.Message {
  getJobId(): string;
  setJobId(value: string): JobUpdate;

  getStatus(): string;
  setStatus(value: string): JobUpdate;

  getProgressPercent(): number;
  setProgressPercent(value: number): JobUpdate;

  getMessage(): string;
  setMessage(value: string): JobUpdate;

  getTimestamp(): number;
  setTimestamp(value: number): JobUpdate;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): JobUpdate.AsObject;
  static toObject(includeInstance: boolean, msg: JobUpdate): JobUpdate.AsObject;
  static serializeBinaryToWriter(message: JobUpdate, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): JobUpdate;
  static deserializeBinaryFromReader(message: JobUpdate, reader: jspb.BinaryReader): JobUpdate;
}

export namespace JobUpdate {
  export type AsObject = {
    jobId: string,
    status: string,
    progressPercent: number,
    message: string,
    timestamp: number,
  }
}

export class Empty extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Empty.AsObject;
  static toObject(includeInstance: boolean, msg: Empty): Empty.AsObject;
  static serializeBinaryToWriter(message: Empty, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Empty;
  static deserializeBinaryFromReader(message: Empty, reader: jspb.BinaryReader): Empty;
}

export namespace Empty {
  export type AsObject = {
  }
}

