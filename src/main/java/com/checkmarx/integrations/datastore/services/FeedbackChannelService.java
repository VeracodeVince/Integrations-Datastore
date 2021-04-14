package com.checkmarx.integrations.datastore.services;

import com.checkmarx.integrations.common.grpc.*;
import com.checkmarx.integrations.datastore.repositories.CxProjectRepository;
import com.checkmarx.integrations.datastore.repositories.FeedbackChannelRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * gRPC service used to get feedback channels for a specified CX platform project from DataStore.
 */
@RequiredArgsConstructor
@GrpcService
public class FeedbackChannelService extends FeedbackChannelsGrpc.FeedbackChannelsImplBase {

    private final CxProjectRepository projectRepo;
    private final FeedbackChannelRepository channelRepo;

    @Override
    public void getFeedbackChannels(CxProject project, StreamObserver<FeedbackChannelResponse> responseObserver) {
        String identity = project.getId();
        if (projectRepo.existsByIdentity(identity)) {
            FeedbackChannelResponse response = getResponse(identity);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            Status status = Status.FAILED_PRECONDITION.withDescription("Invalid project ID.");
            responseObserver.onError(status.asRuntimeException());
        }
    }

    private FeedbackChannelResponse getResponse(String identity) {
        List<FeedbackChannel> channelDtos = channelRepo.getByProjectIdentity(identity)
                .stream()
                .map(toChannelDtos())
                .collect(Collectors.toList());

        return FeedbackChannelResponse.newBuilder()
                .addAllChannels(channelDtos)
                .build();
    }

    private Function<com.checkmarx.integrations.datastore.models.publishing.FeedbackChannel, FeedbackChannel> toChannelDtos() {
        return channelFromDB -> FeedbackChannel.newBuilder()
                .setId(channelFromDB.getId())
                .setName(channelFromDB.getName())
                .setPluginId(channelFromDB.getPluginId())
                .setBody(channelFromDB.getBody().toString())
                .build();
    }
}
