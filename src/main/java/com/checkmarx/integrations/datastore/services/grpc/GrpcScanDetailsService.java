package com.checkmarx.integrations.datastore.services.grpc;

import com.checkmarx.integrations.common.grpc.scandetails.ScanDetailsGrpc;
import com.checkmarx.integrations.common.grpc.scandetails.ScanDetailsRequest;
import com.checkmarx.integrations.common.grpc.scandetails.ScanDetailsResponse;
import com.checkmarx.integrations.datastore.controllers.exceptions.EntityNotFoundException;
import com.checkmarx.integrations.datastore.services.ScanDetailsService;
import com.fasterxml.jackson.databind.JsonNode;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@RequiredArgsConstructor
@GrpcService
public class GrpcScanDetailsService extends  ScanDetailsGrpc.ScanDetailsImplBase {

    private final ScanDetailsService scanDetailsService;

    @Override
    public void getScanDetails(ScanDetailsRequest request, StreamObserver<ScanDetailsResponse> responseObserver) {
        String scanId = request.getScanId();
        try {
            JsonNode scanDetailsByScanId = scanDetailsService.getScanDetailsByScanId(scanId);
            ScanDetailsResponse response = toScanDetailsResponse(scanDetailsByScanId);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (EntityNotFoundException e) {
            Status status = Status.NOT_FOUND.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }
    }

    private ScanDetailsResponse toScanDetailsResponse(JsonNode jsonNode) {
        return ScanDetailsResponse.newBuilder()
                .setBody(jsonNode.toString())
                .build();
    }
}
