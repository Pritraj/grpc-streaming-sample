# Critical Envoy Properties for gRPC Streaming

To ensure reliable gRPC-Web streaming, the following Envoy configurations are essential:

1.  **`http2_protocol_options: {}`** (Cluster)

    - Forces HTTP/2 upstream to the gRPC backend.

2.  **`stream_idle_timeout: 0s`** (HttpConnectionManager)

    - Prevents Envoy from killing idle streams waiting for data.

3.  **`timeout: 0s`** (Route)

    - Disables the default 15s request timeout for long-lived streams.

4.  **`max_stream_duration: { grpc_timeout_header_max: 0s }`** (Route)

    - Respects the gRPC client's timeout header (or infinite).

5.  **`expose_headers: grpc-status,grpc-message`** (CORS)

    - Allows the browser to see stream completion/error status.

6.  **`envoy.filters.http.grpc_web`** (Filter)

    - Translates HTTP/1.1 browser requests to HTTP/2 gRPC.

7.  **`allow_headers`** (CORS)
    - Must include `x-grpc-web`, `content-type`, and `grpc-timeout`.
