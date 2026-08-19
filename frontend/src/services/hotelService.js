import api from "./api";

export const getAllHotels = async () => {
  const response = await api.get("/hotels");

  return response.data;
};

export const getHotelById = async (hotelId) => {
  const response = await api.get(`/hotels/${hotelId}`);

  return response.data;
};

export const createHotel = async (hotelData) => {
  const response = await api.post(
    "/hotels",
    hotelData
  );

  return response.data;
};

export const updateHotel = async (hotelId, hotelData) => {
  const response = await api.put(
    `/hotels/${hotelId}`,
    hotelData
  );

  return response.data;
};

export const deleteHotel = async (hotelId) => {
  const response = await api.delete(`/hotels/${hotelId}`);

  return response.data;
};
