import { createContext, useContext, useState, useEffect } from "react";
import { userApi,authApi,cartApi } from "@/api";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    try {
      const stored = localStorage.getItem("user");
      if (!stored || stored === "undefined") return null;
      return JSON.parse(stored);
    } catch {
      return null;
    }
  });

  const [cartCount, setCartCount] = useState(0);
  const [loading, setLoading] = useState(true);

  // ======================
  // CHECK LOGIN
  // ======================
  const isLoggedIn = () => {
    return !!localStorage.getItem("accessToken");
  };

// fetch user info
const fetchMe = async () => {
    if (!isLoggedIn()) {
      setUser(null);
      setLoading(false);
      return;
    }

    try {
      // 1. Lấy dữ liệu tạm từ máy để hiện giao diện nhanh (UX)
      const cachedUser = localStorage.getItem("user");
      if (cachedUser && cachedUser !== "undefined") {
        setUser(JSON.parse(cachedUser));
      }

      // 2. Gọi API kiểm tra thực tế
      const authRes = await authApi.getMe();

      // Nếu là Admin thì không cho login vào trang User
      if (authRes.role !== "customer") {
        logout(); // Gọi hàm logout ở trên để xóa sạch token rác
        return;
      }

      const profileRes = await userApi.getProfile();
      const fullData = { ...profileRes, role: authRes.role, email: authRes.email };

      setUser(fullData);
      localStorage.setItem("user", JSON.stringify(fullData));
    } catch (err) {
      console.error("Phiên đăng nhập hết hạn hoặc lỗi hệ thống:", err);
      // Nếu lỗi (thường là 401), dọn dẹp luôn để không bị trạng thái "đăng nhập ảo"
      logout(); 
    } finally {
      setLoading(false);
    }
  };

  // ======================
  // GET CART
  // ======================
  const fetchCart = async () => {
    if (!isLoggedIn()) {
      setCartCount(0);
      return;
    }

    try {
      const res = await cartApi.getCart();
      setCartCount(res.items?.length || 0);
    } catch {
      setCartCount(0);
    }
  };

  // ======================
  // INIT APP
  // ======================
useEffect(() => {
    const init = async () => {
      // Nếu là trang admin thì nghỉ khỏe cho AdminAuthContext làm việc
      if (window.location.pathname.startsWith("/admin")) {
        setLoading(false);
        return;
      }

      // Chỉ fetch khi thực sự có token trong máy
      if (isLoggedIn()) {
        await Promise.all([fetchMe(), fetchCart()]);
      } else {
        setLoading(false);
      }
    };

    init();
  }, []);

  // ======================
  // AFTER LOGIN
  // ======================
  const loginSuccess = async () => {
    await fetchMe();
    await fetchCart();
  };

  // ======================
  // LOGOUT
  // ======================
const logout = async () => {
  try {
    await authApi.logout(); // gọi backend
  } catch (err) {
    // có thể fail nhưng vẫn logout local
  }

  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("user");

  setUser(null);
  setCartCount(0);
};

  return (
    <AuthContext.Provider
      value={{
        user,
        cartCount,
        loading,
        loginSuccess,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

// ======================
// HOOK
// ======================
export const useAuthContext = () => useContext(AuthContext);