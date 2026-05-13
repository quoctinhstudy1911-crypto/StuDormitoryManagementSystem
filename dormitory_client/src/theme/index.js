import { createTheme } from '@mui/material';

// Tách theme ra để dễ quản lý và tái sử dụng cho Admin/User sau này
const theme = createTheme({
    palette: {
        primary: {
            main: '#2563eb', // Xanh Portal
        },
        secondary: {
            main: '#059669', // Xanh lá (Duyệt hồ sơ)
        },
        background: {
            default: '#f8fafc',
        },
    },
    typography: {
        fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
        button: {
            textTransform: 'none',
            fontWeight: 500,
        },
    },
    // Ghi đè style mặc định cho các component để không phải viết sx nhiều lần
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: 8, // Bo góc đồng bộ cho tất cả button
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    borderRadius: 12,
                    boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)',
                },
            },
        },
    },
});

export default theme;