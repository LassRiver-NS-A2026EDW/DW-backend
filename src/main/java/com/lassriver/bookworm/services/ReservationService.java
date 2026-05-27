package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.ReservationCreateRequest;
import com.lassriver.bookworm.dtos.response.ReservationResponse;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationCreateRequest request, String authenticatedEmail);

    ReservationResponse cancelReservation(Long reservationId, String authenticatedEmail);

    List<ReservationResponse> getMyReservations(String authenticatedEmail);
}
