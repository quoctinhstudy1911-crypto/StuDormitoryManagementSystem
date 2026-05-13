import { Route, Navigate, Outlet } from "react-router-dom";
import AdminLayout from "../layouts/AdminLayout";
import AdminDashboard from "../pages/admin/AdminDashboard";
import SanPham from "../pages/admin/SanPham";
import DangNhap from "../pages/admin/DangNhap";
import DonHang from "@/pages/admin/DonHang";

// Component bảo vệ route dành cho admin
const RequireAdmin = () => {
  // Kiểm tra token và role từ localStorage
  const token = localStorage.getItem("admin_accessToken");
  const role = localStorage.getItem("admin_role");

  // Nếu không có token admin, chuyển hướng đến trang đăng nhập admin
  if (!token) {
    return <Navigate to="/admin/login" replace />;
  }

  // Nếu role không phải admin, đá về trang chủ của khách
  if (!["admin", "super_admin"].includes(role)) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};

export default function AdminRoutes() {
  return (
    <>
      {/* Route đăng nhập admin */}
      <Route path="/admin/login" element={<DangNhap />} />
      
      {/* Các route admin được bảo vệ */}
      <Route element={<RequireAdmin />}>
        {/* AdminLayout chứa Sidebar và Navbar_admin */}
        <Route element={<AdminLayout />}>
          {/* Lưu ý: index giúp /admin khớp trực tiếp với Dashboard */}
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/admin/products" element={<SanPham />} />
          <Route path="/admin/orders" element={<DonHang />} />
        </Route>
      </Route>
    </>
  );
}