package com.example.election.controllers.test;

import com.example.election.classes.grpc.*;
import com.example.election.classes.mainClasses.Status;
import com.example.election.services.Test;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.security.NoSuchAlgorithmException;

@GrpcService
public class TestController extends roleGrpc.roleImplBase {
    private final Test test;

    public TestController(Test test) {
        this.test = test;
    }

    @Override
    public void getRole(com.example.election.classes.grpc.Test.Role request, StreamObserver<com.example.election.classes.grpc.Test.Response> responseObserver) {
        String name = request.getName();

        com.example.election.classes.grpc.Test.Response.Builder response = com.example.election.classes.grpc.Test.Response.newBuilder();

//        try {
//
////            response.setResponse(test.createAdmin("admin01","superpassword01"));
//            response.setResponse(test.createVoter("theVery1User","qwerty34"));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        response.setResponse(test.setVoter("theVery1User", Status.ACCEPTED));
//        response.setResponse(test.processingList());
//        response.setResponse(test.adminList());
        response.setResponse(
                test.vote("theVery1User",1L,2L)
        );
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();

    }
}
