package com.example.grpc.job

import io.grpc.ServerBuilder

fun main() {
    val port = 9090
    val server = ServerBuilder.forPort(port)
        .addService(JobServiceImpl())
        .build()
        .start()

    println("Server started, listening on $port")

    Runtime.getRuntime().addShutdownHook(Thread {
        println("*** shutting down gRPC server since JVM is shutting down")
        server.shutdown()
        println("*** server shut down")
    })

    server.awaitTermination()
}
