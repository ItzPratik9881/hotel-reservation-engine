import api from "./api";

export const getAllRooms = async () => {
  const response = await api.get("/rooms");
  return response.data.data;
};

export const getRoomById = async (roomId) => {
  const response = await api.get(`/rooms/${roomId}`);
  return response.data.data;
};

export const getRoomsByHotel = async (hotelId) => {
  const response = await api.get(`/rooms/hotel/${hotelId}`);
  return response.data.data;
};