import api from "./api";

export const getAllPayments = async () => {
  const response = await api.get("/payments");

  return response.data;
};

export const createPayment = async (paymentData) => {
  const response = await api.post(
    "/payments",
    paymentData
  );

  return response.data;
};

export const getPaymentById = async (paymentId) => {
  const response = await api.get(
    `/payments/${paymentId}`
  );

  return response.data;
};

export const getPaymentByReservation = async (
  reservationId
) => {
  const response = await api.get(
    `/payments/reservation/${reservationId}`
  );

  return response.data;
};