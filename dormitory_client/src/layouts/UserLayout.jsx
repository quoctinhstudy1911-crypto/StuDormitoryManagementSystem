import { Outlet } from "react-router-dom";
import MyNavbar from "@/components/user/Navbar";

export default function UserLayout() {
  
  return (
<div className="user-layout min-vh-100 d-flex flex-column">
  
    {/* Navbar luôn hiển thị ở tất cả các trang con của UserLayout */}     
   <MyNavbar />

      {/* Phần main sẽ hiển thị các component con của UserLayout, ví dụ như Trangchu, Dangki, Dangnhap, Profile,... */}
      <main>
        <Outlet /> {/* Nơi hiển thị các component con của UserLayout */}
      </main>

</div>
    
  );
}