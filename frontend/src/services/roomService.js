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

export const createRoom = async (roomData) => {
  const response = await api.post("/rooms", roomData);
  return response.data;
};

export const updateRoom = async (roomId, roomData) => {
  const response = await api.put(`/rooms/${roomId}`, roomData);
  return response.data;
};

export const deleteRoom = async (roomId) => {
  const response = await api.delete(`/rooms/${roomId}`);
  return response.data;
};
