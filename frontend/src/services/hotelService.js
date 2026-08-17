import api from "./api";

export const getAllHotels = async () => {
  const response = await api.get("/hotels");
  return response.data.data;
};

export const getHotelById = async (hotelId) => {
  const response = await api.get(`/hotels/${hotelId}`);
  return response.data.data;
};