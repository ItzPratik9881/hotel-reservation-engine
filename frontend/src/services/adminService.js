import api from "./api";

export const getAdminDashboard = async () => {
  const response = await api.get("/dashboard");
  return response.data;
};

export const getAdminRevenue = async () => {
  const response = await api.get("/dashboard/revenue");
  return response.data;
};

export const getBookingAnalytics = async () => {
  const response = await api.get("/dashboard/bookings");
  return response.data;
};