import { Outlet } from "react-router-dom";
import NavbarAdmin from "../components/admin/Navbar_admin";
import { useAuthContext } from "../context/AuthContext";

export default function AdminLayout() {
  const { user, logout } = useAuthContext();

  const navItems = [
    { path: "/admin", label: "Dashboard", icon: "📊" },
    { path: "/admin/products", label: "Sản phẩm", icon: "📦" },
    { path: "/admin/orders", label: "Đơn hàng", icon: "🛒" },
    { path: "/admin/users", label: "Người dùng", icon: "👥" },
    { path: "/admin/settings", label: "Cài đặt", icon: "⚙️" },
  ];

  const isActive = (path) => window.location.pathname === path;

  return (
    <div style={{
      display: "flex",
      flexDirection: "column",
      minHeight: "100vh",
      backgroundColor: "#f9fafb",
    }}>
      {/* Navbar */}
      <NavbarAdmin user={user} logout={logout} />

      {/* Main Content */}
      <div style={{
        display: "flex",
        flex: 1,
        overflow: "hidden",
      }}>
        {/* Sidebar */}
        <aside style={{
          width: "260px",
          backgroundColor: "#ffffff",
          borderRight: "1px solid #e5e7eb",
          overflowY: "auto",
          padding: "24px 0",
        }}>
          <nav style={{
            display: "flex",
            flexDirection: "column",
            gap: "4px",
          }}>
            {navItems.map((item) => (
              <a
                key={item.path}
                href={item.path}
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: "12px",
                  padding: "12px 20px",
                  color: isActive(item.path) ? "#3b82f6" : "#6b7280",
                  textDecoration: "none",
                  fontSize: "14px",
                  fontWeight: isActive(item.path) ? "600" : "500",
                  background: isActive(item.path) ? "#eff6ff" : "transparent",
                  borderLeft: isActive(item.path) ? "3px solid #3b82f6" : "3px solid transparent",
                  transition: "all 0.2s",
                  cursor: "pointer",
                }}
                onMouseEnter={(e) => {
                  if (!isActive(item.path)) {
                    e.currentTarget.style.background = "#f3f4f6";
                    e.currentTarget.style.color = "#374151";
                  }
                }}
                onMouseLeave={(e) => {
                  if (!isActive(item.path)) {
                    e.currentTarget.style.background = "transparent";
                    e.currentTarget.style.color = "#6b7280";
                  }
                }}
              >
                <span style={{ fontSize: "18px" }}>{item.icon}</span>
                {item.label}
              </a>
            ))}
          </nav>

          {/* Sidebar Footer */}
          <div style={{
            marginTop: "32px",
            paddingTop: "24px",
            borderTop: "1px solid #e5e7eb",
            padding: "24px 20px",
          }}>
            <div style={{
              padding: "12px",
              backgroundColor: "#fef3c7",
              borderRadius: "8px",
              fontSize: "13px",
              color: "#78350f",
              lineHeight: "1.5",
            }}>
              <strong>Lưu ý:</strong> Đây là khu vực quản trị. Vui lòng đảm bảo bạn có quyền truy cập trước khi thực hiện bất kỳ thay đổi nào.
            </div>
          </div>
        </aside>

        {/* Main Content Area */}
        <main style={{
          flex: 1,
          overflowY: "auto",
          padding: "32px",
        }}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
