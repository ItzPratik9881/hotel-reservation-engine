import api from "./api";

export const createReservation = async (reservationData) => {
  const response = await api.post(
    "/reservations",
    reservationData
  );

  return response.data;
};

export const getReservationById = async (reservationId) => {
  const response = await api.get(
    `/reservations/${reservationId}`
  );

  return response.data;
};

export const getUserReservations = async (userId) => {
  const response = await api.get(
    `/reservations/user/${userId}`
  );

  return response.data;
};

export const cancelReservation = async (reservationId) => {
  const response = await api.put(
    `/reservations/${reservationId}/cancel`
  );

  return response.data;
};