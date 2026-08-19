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

export const getAllReservations = async () => {
  const response = await api.get("/reservations");

  return response.data;
};

export const getReservationsByRoom = async (roomId) => {
  const response = await api.get(
    `/reservations/room/${roomId}`
  );

  return response.data;
};

export const cancelReservation = async (reservationId) => {
  const response = await api.put(
    `/reservations/${reservationId}/cancel`
  );

  return response.data;
};

export const checkInReservation = async (reservationId) => {
  const response = await api.put(
    `/reservations/${reservationId}/check-in`
  );

  return response.data;
};

export const checkOutReservation = async (reservationId) => {
  const response = await api.put(
    `/reservations/${reservationId}/check-out`
  );

  return response.data;
};
