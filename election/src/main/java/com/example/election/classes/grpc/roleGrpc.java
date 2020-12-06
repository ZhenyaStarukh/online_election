package com.example.election.classes.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: test.proto")
public final class roleGrpc {

  private roleGrpc() {}

  public static final String SERVICE_NAME = "role";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.election.classes.grpc.Test.Role,
      com.example.election.classes.grpc.Test.Response> getGetRoleMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getRole",
      requestType = com.example.election.classes.grpc.Test.Role.class,
      responseType = com.example.election.classes.grpc.Test.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.election.classes.grpc.Test.Role,
      com.example.election.classes.grpc.Test.Response> getGetRoleMethod() {
    io.grpc.MethodDescriptor<com.example.election.classes.grpc.Test.Role, com.example.election.classes.grpc.Test.Response> getGetRoleMethod;
    if ((getGetRoleMethod = roleGrpc.getGetRoleMethod) == null) {
      synchronized (roleGrpc.class) {
        if ((getGetRoleMethod = roleGrpc.getGetRoleMethod) == null) {
          roleGrpc.getGetRoleMethod = getGetRoleMethod = 
              io.grpc.MethodDescriptor.<com.example.election.classes.grpc.Test.Role, com.example.election.classes.grpc.Test.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "role", "getRole"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.election.classes.grpc.Test.Role.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.election.classes.grpc.Test.Response.getDefaultInstance()))
                  .setSchemaDescriptor(new roleMethodDescriptorSupplier("getRole"))
                  .build();
          }
        }
     }
     return getGetRoleMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static roleStub newStub(io.grpc.Channel channel) {
    return new roleStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static roleBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new roleBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static roleFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new roleFutureStub(channel);
  }

  /**
   */
  public static abstract class roleImplBase implements io.grpc.BindableService {

    /**
     */
    public void getRole(com.example.election.classes.grpc.Test.Role request,
        io.grpc.stub.StreamObserver<com.example.election.classes.grpc.Test.Response> responseObserver) {
      asyncUnimplementedUnaryCall(getGetRoleMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetRoleMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.example.election.classes.grpc.Test.Role,
                com.example.election.classes.grpc.Test.Response>(
                  this, METHODID_GET_ROLE)))
          .build();
    }
  }

  /**
   */
  public static final class roleStub extends io.grpc.stub.AbstractStub<roleStub> {
    private roleStub(io.grpc.Channel channel) {
      super(channel);
    }

    private roleStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected roleStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new roleStub(channel, callOptions);
    }

    /**
     */
    public void getRole(com.example.election.classes.grpc.Test.Role request,
        io.grpc.stub.StreamObserver<com.example.election.classes.grpc.Test.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetRoleMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class roleBlockingStub extends io.grpc.stub.AbstractStub<roleBlockingStub> {
    private roleBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private roleBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected roleBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new roleBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.example.election.classes.grpc.Test.Response getRole(com.example.election.classes.grpc.Test.Role request) {
      return blockingUnaryCall(
          getChannel(), getGetRoleMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class roleFutureStub extends io.grpc.stub.AbstractStub<roleFutureStub> {
    private roleFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private roleFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected roleFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new roleFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.election.classes.grpc.Test.Response> getRole(
        com.example.election.classes.grpc.Test.Role request) {
      return futureUnaryCall(
          getChannel().newCall(getGetRoleMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_ROLE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final roleImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(roleImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_ROLE:
          serviceImpl.getRole((com.example.election.classes.grpc.Test.Role) request,
              (io.grpc.stub.StreamObserver<com.example.election.classes.grpc.Test.Response>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class roleBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    roleBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.election.classes.grpc.Test.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("role");
    }
  }

  private static final class roleFileDescriptorSupplier
      extends roleBaseDescriptorSupplier {
    roleFileDescriptorSupplier() {}
  }

  private static final class roleMethodDescriptorSupplier
      extends roleBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    roleMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (roleGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new roleFileDescriptorSupplier())
              .addMethod(getGetRoleMethod())
              .build();
        }
      }
    }
    return result;
  }
}
