import axios from "axios";

const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    timeout: 10000,
});

axiosClient.interceptors.response.use(
    (response) => {
        const res = response.data;

        // 1. Xử lý lỗi nghiệp vụ (Backend trả về success: false)
        if (res && res.success === false) {
            return Promise.reject({
                message: res.message || "Lỗi hệ thống",
                status: response.status,
                isLogicError: true, // Đánh dấu đây là lỗi logic (ví dụ: Hết đợt) chứ không phải lỗi sập mạng
            });
        }

        // 2. Trả về dữ liệu sạch
        return (res && res.data !== undefined) ? res.data : res;
    },
    (error) => {
        // 3. Xử lý lỗi kết nối hoặc lỗi HTTP (404, 500, Timeout)
        return Promise.reject({
            message: error.response?.data?.message || "Không thể kết nối đến máy chủ",
            status: error.response?.status,
            isLogicError: false, // Lỗi kỹ thuật thực sự
        });
    }
);

export default axiosClient;