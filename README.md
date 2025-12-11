# gRPC Streaming Sample

This project demonstrates a real-time job monitoring application using gRPC streaming, Envoy proxy, and Angular (Signals).

## Architecture

- **Backend**: Java 21 gRPC Server (simulates long-running jobs)
- **Proxy**: Envoy (transcodes gRPC-Web to gRPC)
- **Frontend**: Angular 18 (Signals, Standalone Components)

## Prerequisites

- **Docker Desktop** (Must be running)
- **Node.js** (Optional, for local dev)
- **Java 21** (Optional, for local dev)

## How to Run

1. **Ensure Docker is running**.
2. Run the application using Docker Compose:

   ```bash
   docker-compose up --build
   ```

3. Open your browser to [http://localhost:4200](http://localhost:4200).
4. Click "Start New Job" to see the real-time streaming updates from the Java backend.

## Development

### Server

- Located in `server/`
- Build: `./gradlew build`
- Run: `./gradlew run`

### Client

- Located in `client/`
- Install: `npm install`
- Run: `npm start` (Runs on port 4200, requires Envoy running on 8080)

### Envoy

- Configuration in `envoy/envoy.yaml`
- Must be run via Docker to handle gRPC-Web transcoding.
