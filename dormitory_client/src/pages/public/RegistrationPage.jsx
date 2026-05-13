import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
    TextField, Button, Typography, Grid, Box, Container, Paper,
    Stepper, Step, StepLabel, Alert, CircularProgress, Card,
    Stack, Divider, Fade, Zoom
} from "@mui/material";
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';

import { applicationApi, periodApi, documentApi } from "@/api";

const STEPS = ['Kiểm tra đợt', 'Thông tin cá nhân', 'Tải hồ sơ', 'Hoàn tất'];

export default function RegistrationPage() {
    const navigate = useNavigate();
    const [activeStep, setActiveStep] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [period, setPeriod] = useState(null);
    const [fetchingPeriod, setFetchingPeriod] = useState(true);

    const [formData, setFormData] = useState({ cccd: '', fullName: '' });
    const [appId, setAppId] = useState(null);
    const [uploadedDocs, setUploadedDocs] = useState([]);

    useEffect(() => {
        fetchCurrentPeriod();
    }, []);

    const fetchCurrentPeriod = async () => {
        setFetchingPeriod(true);
        setError(null);
        try {
            const data = await periodApi.getCurrent();
            setPeriod(data);
        } catch (err) {
            if (err.status !== 400) {
                setError(err.message || "Không thể kết nối đến máy chủ.");
            }
            setPeriod(null);
        } finally {
            setFetchingPeriod(false);
        }
    };

    // --- PHẦN LOGIC ĐÃ ĐƯỢC CHỈNH SỬA ---
    const handleNext = async () => {
        setError(null);

        // Xử lý logic tại Bước 1 (Thông tin cá nhân) trước khi sang Bước 2
        if (activeStep === 1) {
            if (!formData.cccd.trim() || !formData.fullName.trim()) {
                setError("Vui lòng không bỏ trống thông tin cá nhân.");
                return;
            }

            setLoading(true);
            try {
                // Chỉ gửi cccd và periodId đúng theo ApplicationRequestDTO
                const payload = {
                    cccd: formData.cccd.trim(),
                    periodId: period?.id
                };

                const res = await applicationApi.create(payload);

                // Lưu ID nhận được từ Backend để dùng cho việc upload tài liệu
                setAppId(res.id);
                setActiveStep(2);
            } catch (err) {
                // Lấy thông báo lỗi chi tiết từ server nếu có
                const msg = err.response?.data?.message || err.response?.data || err.message;
                setError(typeof msg === 'string' ? msg : "Lỗi xác thực dữ liệu từ máy chủ.");
            } finally {
                setLoading(false);
            }
            return; // Thoát hàm để không chạy xuống setActiveStep phía dưới
        }

        // Xử lý logic tại Bước 2 (Tải hồ sơ) trước khi hoàn tất
        if (activeStep === 2) {
            if (uploadedDocs.length === 0) {
                setError("Vui lòng tải lên ít nhất 01 chứng từ minh chứng.");
                return;
            }
            setActiveStep(3);
            return;
        }

        // Các bước chuyển tiếp thông thường (0 -> 1 hoặc 3 -> kết thúc)
        setActiveStep((prev) => prev + 1);
    };
    // --- HẾT PHẦN CHỈNH SỬA LOGIC ---

    const handleUpload = async (type) => {
        setLoading(true);
        setError(null);
        try {
            const mockUrl = `https://storage.ktx.vn/files/${type}.pdf`;
            await documentApi.upload(appId, type, mockUrl);
            if (!uploadedDocs.includes(type)) setUploadedDocs([...uploadedDocs, type]);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    if (fetchingPeriod) {
        return (
            <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '80vh', gap: 2 }}>
                <CircularProgress thickness={4} size={50} sx={{ color: 'primary.main' }} />
                <Typography sx={{ fontWeight: 500, color: 'text.secondary', letterSpacing: 1 }}>ĐANG KIỂM TRA HỆ THỐNG...</Typography>
            </Box>
        );
    }

    return (
        <Container maxWidth="md" sx={{ py: { xs: 4, md: 8 } }}>
            <Fade in timeout={800}>
                <Paper
                    elevation={0}
                    sx={{
                        p: { xs: 3, md: 6 },
                        borderRadius: 6,
                        border: '1px solid',
                        borderColor: 'divider',
                        boxShadow: '0 20px 50px rgba(0,0,0,0.05)',
                        position: 'relative',
                        overflow: 'hidden'
                    }}
                >
                    <Box sx={{ position: 'absolute', top: 0, right: 0, width: 150, height: 150, background: 'linear-gradient(135deg, rgba(37, 99, 235, 0.05) 0%, transparent 100%)', borderRadius: '0 0 0 100%' }} />

                    <Typography variant="h4" fontWeight="900" textAlign="center" mb={1} color="primary.main" sx={{ letterSpacing: -0.5 }}>
                        Đăng Ký Nội Trú
                    </Typography>
                    <Typography variant="body2" textAlign="center" color="text.secondary" mb={5}>
                        Hệ thống quản lý cư trú sinh viên trực tuyến
                    </Typography>

                    <Stepper activeStep={activeStep} alternativeLabel sx={{ mb: 8 }}>
                        {STEPS.map((label) => (
                            <Step key={label}>
                                <StepLabel sx={{ '& .MuiStepLabel-label': { fontWeight: 600, fontSize: '0.85rem' } }}>{label}</StepLabel>
                            </Step>
                        ))}
                    </Stepper>

                    <Box sx={{ minHeight: '300px' }}>
                        {activeStep === 0 && (
                            <Zoom in>
                                <Box>
                                    {period ? (
                                        <Stack spacing={3}>
                                            <Alert severity="success" variant="filled" icon={<CheckCircleIcon />} sx={{ borderRadius: 4, fontWeight: 600 }}>
                                                Hệ thống đã sẵn sàng tiếp nhận hồ sơ!
                                            </Alert>
                                            <Card sx={{ p: 4, textAlign: 'center', borderRadius: 5, border: '2px solid', borderColor: 'primary.50', bgcolor: 'primary.50/10', boxShadow: 'none' }}>
                                                <Typography variant="overline" color="primary" fontWeight="bold">Thông tin đợt hiện tại</Typography>
                                                <Typography variant="h5" fontWeight="800" sx={{ my: 1, color: 'text.primary' }}>{period.name}</Typography>
                                                <Divider sx={{ my: 2, mx: 'auto', width: '60%' }} />
                                                <Stack direction="row" justifyContent="center" spacing={4}>
                                                    <Box>
                                                        <Typography variant="caption" display="block" color="text.secondary">NGÀY BẮT ĐẦU</Typography>
                                                        <Typography variant="subtitle1" fontWeight="700">{period.startDate}</Typography>
                                                    </Box>
                                                    <Box>
                                                        <Typography variant="caption" display="block" color="text.secondary">NGÀY KẾT THÚC</Typography>
                                                        <Typography variant="subtitle1" fontWeight="700">{period.endDate}</Typography>
                                                    </Box>
                                                </Stack>
                                            </Card>
                                        </Stack>
                                    ) : (
                                        <Box sx={{ textAlign: 'center', py: 4 }}>
                                            <EventBusyIcon sx={{ fontSize: 80, color: 'action.disabled', mb: 2 }} />
                                            <Typography variant="h6" fontWeight="700" color="text.secondary">Hiện không có đợt đăng ký</Typography>
                                            <Typography variant="body2" color="text.disabled" mb={3}>Hệ thống đang đóng hoặc đã hết thời gian nộp hồ sơ.</Typography>
                                            {error && <Alert severity="error" sx={{ borderRadius: 3, textAlign: 'left' }}>{error}</Alert>}
                                        </Box>
                                    )}
                                </Box>
                            </Zoom>
                        )}

                        {activeStep === 1 && (
                            <Fade in>
                                <Stack spacing={4}>
                                    <Box sx={{ p: 2, bgcolor: 'info.50', borderRadius: 3, display: 'flex', gap: 2, alignItems: 'center' }}>
                                        <InfoOutlinedIcon color="info" />
                                        <Typography variant="body2" color="info.main" fontWeight={500}>
                                            Vui lòng nhập chính xác thông tin theo giấy tờ tùy thân.
                                        </Typography>
                                    </Box>
                                    <Grid container spacing={3}>
                                        <Grid item xs={12} md={6}>
                                            <TextField
                                                fullWidth label="Số Căn cước công dân"
                                                variant="filled"
                                                value={formData.cccd} onChange={(e) => setFormData({...formData, cccd: e.target.value})}
                                                InputProps={{ disableUnderline: true, sx: { borderRadius: 3 } }}
                                            />
                                        </Grid>
                                        <Grid item xs={12} md={6}>
                                            <TextField
                                                fullWidth label="Họ và tên đầy đủ"
                                                variant="filled"
                                                value={formData.fullName} onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                                                InputProps={{ disableUnderline: true, sx: { borderRadius: 3 } }}
                                            />
                                        </Grid>
                                    </Grid>
                                    {error && <Alert severity="error" variant="outlined" sx={{ borderRadius: 3 }}>{error}</Alert>}
                                </Stack>
                            </Fade>
                        )}

                        {activeStep === 2 && (
                            <Fade in>
                                <Stack spacing={3}>
                                    <Typography variant="subtitle1" fontWeight="700">Danh mục hồ sơ yêu cầu</Typography>
                                    {['CCCD', 'PRIORITY'].map((type) => (
                                        <Card key={type} variant="outlined" sx={{ p: 2.5, display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderRadius: 4 }}>
                                            <Box>
                                                <Typography fontWeight="700" variant="body1">
                                                    {type === 'CCCD' ? "Chứng minh nhân dân / CCCD" : "Giấy tờ ưu tiên đối tượng"}
                                                </Typography>
                                                <Typography variant="caption" color="text.secondary">Yêu cầu: Bản quét màu hoặc ảnh chụp rõ nét</Typography>
                                            </Box>
                                            <Button
                                                variant={uploadedDocs.includes(type) ? "contained" : "outlined"}
                                                startIcon={uploadedDocs.includes(type) ? <CheckCircleIcon /> : <CloudUploadIcon />}
                                                onClick={() => handleUpload(type)}
                                                color={uploadedDocs.includes(type) ? "success" : "primary"}
                                                disabled={loading}
                                                sx={{ borderRadius: 3 }}
                                            >
                                                {uploadedDocs.includes(type) ? "Đã xong" : "Tải lên"}
                                            </Button>
                                        </Card>
                                    ))}
                                    {error && <Alert severity="error" sx={{ borderRadius: 3 }}>{error}</Alert>}
                                </Stack>
                            </Fade>
                        )}

                        {activeStep === 3 && (
                            <Zoom in>
                                <Box textAlign="center" py={2}>
                                    <CheckCircleIcon color="success" sx={{ fontSize: 100, mb: 3 }} />
                                    <Typography variant="h5" fontWeight="800" gutterBottom>Gửi hồ sơ thành công!</Typography>
                                    <Typography color="text.secondary" mb={4}>
                                        Mã hồ sơ: <Typography component="span" fontWeight="800" color="primary">#{appId}</Typography>
                                    </Typography>
                                    <Card sx={{ p: 3, bgcolor: 'grey.50', borderRadius: 4, textAlign: 'left', border: 'none' }}>
                                        <Typography variant="subtitle2" fontWeight="bold">Các bước tiếp theo:</Typography>
                                        <Typography variant="body2" color="text.secondary">1. Ban quản lý sẽ kiểm duyệt hồ sơ trong 3-5 ngày.</Typography>
                                        <Typography variant="body2" color="text.secondary">2. Thông báo kết quả sẽ được gửi qua mục Tra cứu.</Typography>
                                    </Card>
                                </Box>
                            </Zoom>
                        )}
                    </Box>

                    <Divider sx={{ my: 5 }} />

                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Button
                            startIcon={<ArrowBackIosNewIcon sx={{ fontSize: '1rem !important' }} />}
                            onClick={() => { setActiveStep(prev => prev - 1); setError(null); }}
                            disabled={activeStep === 0 || activeStep === 3 || loading}
                            sx={{ borderRadius: 3, px: 3, fontWeight: 700 }}
                        >
                            Quay lại
                        </Button>

                        <Button
                            variant="contained"
                            size="large"
                            onClick={activeStep === 3 ? () => navigate("/") : handleNext}
                            disabled={loading || (activeStep === 0 && !period)}
                            sx={{ px: 6, py: 1.5, borderRadius: 4, fontWeight: 800 }}
                        >
                            {loading ? <CircularProgress size={24} color="inherit" /> : (activeStep === 3 ? "Về trang chủ" : "Tiếp tục")}
                        </Button>
                    </Box>
                </Paper>
            </Fade>
        </Container>
    );
}