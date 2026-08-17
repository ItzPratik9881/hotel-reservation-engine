import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Hotels from "./pages/Hotels";
import Rooms from "./pages/Rooms";
import Booking from "./pages/Booking";
import Reservations from "./pages/Reservations";
import ProtectedRoute from "./routes/ProtectedRoute";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* Public routes */}
        <Route
          path="/"
          element={<Navigate to="/login" replace />}
        />

        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/register"
          element={<Register />}
        />

        {/* Protected customer routes */}
        <Route element={<ProtectedRoute />}>

          <Route
            path="/dashboard"
            element={<Dashboard />}
          />

          <Route
            path="/hotels"
            element={<Hotels />}
          />

          {/* Hotel rooms */}
          <Route
            path="/hotels/:hotelId/rooms"
            element={<Rooms />}
          />

          <Route
            path="/rooms/:roomId/book"
            element={<Booking />}
          />

          <Route
            path="/reservations"
            element={<Reservations />}
          />

        </Route>

        {/* Unknown route */}
        <Route
          path="*"
          element={<Navigate to="/login" replace />}
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;