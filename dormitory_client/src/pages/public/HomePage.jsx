import { useState } from "react";
import { useNavigate, Link as RouterLink } from "react-router-dom";
import {
    Box, Container, Button, Typography, Grid, Card, TextField, Paper, Stack, InputAdornment, useTheme
} from "@mui/material";
import SearchIcon from '@mui/icons-material/Search';
import AssignmentIndIcon from '@mui/icons-material/AssignmentInd';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';

export default function HomePage() {
    const navigate = useNavigate();
    const theme = useTheme();
    const [searchCccd, setSearchCccd] = useState('');

    const handleSearch = () => {
        if (searchCccd.trim()) {
            navigate(`/status?cccd=${searchCccd.trim()}`);
        }
    };

    const handleSearchKeyDown = (e) => {
        if (e.key === 'Enter') handleSearch();
    };

    return (
        <Box sx={{ bgcolor: "background.default", pb: 10 }}>
            {/* HERO SECTION */}
            <Box
                sx={{
                    background: `linear-gradient(135deg, ${theme.palette.primary.dark} 0%, ${theme.palette.primary.main} 100%)`,
                    color: "common.white",
                    pt: { xs: 8, md: 12 },
                    pb: { xs: 12, md: 16 },
                    textAlign: "center",
                }}
            >
                <Container maxWidth="md">
                    <Typography variant="h3" fontWeight="800" mb={2} sx={{ fontSize: { xs: '2.2rem', md: '3.5rem' }, letterSpacing: '-1px' }}>
                        Đăng ký KTX chỉ trong 3 bước
                    </Typography>

                    <Typography variant="h6" sx={{ opacity: 0.9, mb: 5, fontWeight: 300 }}>
                        Hệ thống nộp hồ sơ và xét duyệt ký túc xá trực tuyến hiện đại, nhanh chóng.
                    </Typography>

                    {/* THANH TÌM KIẾM - FIX 1 HÀNG */}
                    <Paper
                        elevation={0}
                        sx={{
                            p: 0.6,
                            borderRadius: 4,
                            display: "flex",
                            alignItems: "center", // Thẳng hàng dọc
                            maxWidth: 640,
                            mx: "auto",
                            boxShadow: '0 20px 25px -5px rgba(0,0,0,0.1)',
                            bgcolor: 'white'
                        }}
                    >
                        <TextField
                            fullWidth
                            placeholder="Nhập số CCCD/CMND..."
                            value={searchCccd}
                            onChange={(e) => setSearchCccd(e.target.value)}
                            onKeyDown={handleSearchKeyDown}
                            variant="standard" // Dùng standard để dễ custom không viền
                            sx={{ px: 2 }}
                            InputProps={{
                                disableUnderline: true, // Ẩn gạch chân
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon color="action" fontSize="small" />
                                    </InputAdornment>
                                ),
                            }}
                        />
                        <Button
                            variant="contained"
                            onClick={handleSearch}
                            sx={{
                                px: { xs: 2, md: 4 },
                                py: 1.2,
                                borderRadius: 3,
                                fontWeight: 'bold',
                                whiteSpace: 'nowrap', // Ép chữ không rớt dòng
                                minWidth: 'fit-content',
                                boxShadow: 'none'
                            }}
                        >
                            Tra cứu
                        </Button>
                    </Paper>
                </Container>
            </Box>

            {/* Các Card bên dưới giữ nguyên nội dung cũ */}
            <Container maxWidth="lg" sx={{ mt: -8 }}>
                <Grid container spacing={4} justifyContent="center">
                    <Grid item xs={12} md={5}>
                        <Card sx={{ p: 4, borderRadius: 4, transition: '0.3s', '&:hover': { transform: 'translateY(-8px)' } }}>
                            <AssignmentIndIcon sx={{ fontSize: 48, color: "primary.main", mb: 2 }} />
                            <Typography variant="h5" fontWeight="700" gutterBottom>Đăng ký mới</Typography>
                            <Typography color="text.secondary" mb={4} sx={{ minHeight: 48 }}>
                                Dành cho sinh viên chưa có hồ sơ hoặc muốn đăng ký mới cho học kỳ này.
                            </Typography>
                            <Button fullWidth variant="contained" size="large" component={RouterLink} to="/register" sx={{ borderRadius: 3 }}>
                                Bắt đầu đăng ký ngay
                            </Button>
                        </Card>
                    </Grid>
                    <Grid item xs={12} md={5}>
                        <Card sx={{ p: 4, borderRadius: 4, transition: '0.3s', '&:hover': { transform: 'translateY(-8px)' } }}>
                            <CheckCircleOutlineIcon sx={{ fontSize: 48, color: "secondary.main", mb: 2 }} />
                            <Typography variant="h5" fontWeight="700" gutterBottom>Theo dõi trạng thái</Typography>
                            <Typography color="text.secondary" mb={4} sx={{ minHeight: 48 }}>
                                Kiểm tra kết quả xét duyệt, bổ sung hồ sơ hoặc xem thông tin nhận phòng.
                            </Typography>
                            <Button fullWidth variant="outlined" size="large" component={RouterLink} to="/status" sx={{ borderRadius: 3 }}>
                                Xem trạng thái hồ sơ
                            </Button>
                        </Card>
                    </Grid>
                </Grid>
            </Container>
        </Box>
    );
}