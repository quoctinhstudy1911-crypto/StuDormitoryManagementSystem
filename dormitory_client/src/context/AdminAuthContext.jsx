import { authApi } from "@/api";
import { createContext, useContext, useState, useEffect } from "react";

// 1. Khởi tạo Context
const AdminAuthContext = createContext();

export const AdminAuthProvider = ({ children }) => {
  const [admin, setAdmin] = useState(null);
  const [loading, setLoading] = useState(true);

  // ======================
  // INIT (load từ localStorage)
  // ======================
  useEffect(() => {
    const storedAdmin = localStorage.getItem("admin_info");
    const role = localStorage.getItem("admin_role");

    if (storedAdmin && ["admin", "super_admin"].includes(role)) {
      try {
        setAdmin(JSON.parse(storedAdmin));
      } catch {
        setAdmin(null);
        localStorage.removeItem("admin_info");
      }
    }
    setLoading(false);
  }, []);

  // ======================
  // LOGIN
  // ======================
  const loginAdmin = (adminData, role) => {
    localStorage.setItem("admin_info", JSON.stringify(adminData));
    localStorage.setItem("admin_role", role);
    setAdmin(adminData);
  };

  // ======================
  // LOGOUT
  // ======================
  const logoutAdmin = async () => {
    try {
      await authApi.logout(); 
    } catch (err) {
    }

    localStorage.removeItem("admin_info");
    localStorage.removeItem("admin_role");
    localStorage.removeItem("admin_accessToken");
    localStorage.removeItem("admin_refreshToken");

    setAdmin(null);
  };

  return (
    <AdminAuthContext.Provider value={{ admin, loading, loginAdmin, logoutAdmin }}>
      {children}
    </AdminAuthContext.Provider>
  );
};

// ======================
// 2. EXPORT HOOK (ĐÂY LÀ CHỖ BẠN ĐANG THIẾU)
// ======================
export const useAdminAuth = () => {
  const context = useContext(AdminAuthContext);
  if (!context) {
    throw new Error("useAdminAuth must be used within an AdminAuthProvider");
  }
  return context;
};