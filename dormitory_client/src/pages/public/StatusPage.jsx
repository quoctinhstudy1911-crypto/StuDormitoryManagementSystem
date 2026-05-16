import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import {
    TextField, Button, Typography, Box, Container, Paper, Card, CardContent,
    CircularProgress, Alert, Fade, Zoom, Stack, InputAdornment, useTheme,
    Divider, Chip, Grid, Stepper, Step, StepLabel, Avatar
} from "@mui/material";
import SearchIcon from '@mui/icons-material/Search';
import AssignmentIndIcon from '@mui/icons-material/AssignmentInd';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import DescriptionIcon from '@mui/icons-material/Description';
import InfoIcon from '@mui/icons-material/Info';

import { applicationApi, periodApi, documentApi } from "@/api";

const STATUS_COLORS = {
    PENDING: 'warning',
    VERIFIED: 'info',
    APPROVED: 'success',
    REJECTED: 'error',
    CHECKED_IN: 'secondary',
};

const STATUS_LABELS = {
    PENDING: 'Chờ xác minh',
    VERIFIED: 'Đã xác minh',
    APPROVED: 'Được duyệt',
    REJECTED: 'Bị từ chối',
    CHECKED_IN: 'Đã nhập ký túc xá',
};

export default function StatusPage() {
    const [searchParams] = useSearchParams();
    const theme = useTheme();

    const [cccd, setCccd] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [application, setApplication] = useState(null);
    const [documents, setDocuments] = useState([]);
    const [currentPeriod, setCurrentPeriod] = useState(null);

    useEffect(() => {
        const initStatusPage = async () => {
            let periodData = null;
            try {
                const res = await periodApi.getCurrent();
                periodData = res.data || res;
                setCurrentPeriod(periodData);
            } catch (err) {
                console.error("Lỗi tải thông tin đợt");
            }

            const cccdFromUrl = searchParams.get("cccd");
            if (cccdFromUrl) {
                setCccd(cccdFromUrl);
                if (periodData?.id) {
                    executeSearch(cccdFromUrl, periodData.id);
                }
            }
        };
        initStatusPage();
    }, [searchParams]);

    const executeSearch = async (targetCccd, periodId) => {
        setLoading(true);
        setError(null);
        try {
            const response = await applicationApi.getStatus({ cccd: targetCccd, periodId });
            const appData = response.data || response;
            setApplication(appData);

            // Tải thêm danh sách tài liệu
            const docsData = await documentApi.getByApplication(appData.id);
            setDocuments(docsData || []);
        } catch (err) {
            setError(err.message || 'Không tìm thấy hồ sơ đăng ký');
            setApplication(null);
        } finally {
            setLoading(false);
        }
    };

    const getStatusStep = () => {
        const statusOrder = ['PENDING', 'VERIFIED', 'APPROVED', 'CHECKED_IN'];
        return Math.max(0, statusOrder.indexOf(application?.status || 'PENDING'));
    };

    return (
        <Box sx={{ minHeight: "100vh", bgcolor: "#f8fafc", py: 8 }}>
            <Container maxWidth="md">
                <Fade in timeout={800}>
                    <Paper elevation={0} sx={{ borderRadius: "24px", overflow: "hidden", border: "1px solid", borderColor: "divider", boxShadow: '0 4px 20px rgba(0,0,0,0.03)' }}>
                        {/* Header */}
                        <Box sx={{ p: 4, bgcolor: theme.palette.primary.main, color: "primary.contrastText", textAlign: "center" }}>
                            {currentPeriod && (
                                <Box sx={{ mb: 1.5 }}>
                                    <Typography variant="caption" sx={{ px: 2, py: 0.5, bgcolor: "rgba(255,255,255,0.15)", borderRadius: "12px", display: "inline-block", fontWeight: 600, color: "#fef08a", textTransform: 'uppercase', letterSpacing: 1 }}>
                                        Hệ thống đang mở: {currentPeriod.name}
                                    </Typography>
                                </Box>
                            )}
                            <Typography variant="h5" fontWeight="900" letterSpacing={1}>TRA CỨU KẾT QUẢ</Typography>
                        </Box>

                        <Box sx={{ p: { xs: 3, md: 6 } }}>
                            {/* Search Bar */}
                            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 6 }}>
                                <TextField
                                    fullWidth placeholder="Nhập số Căn cước công dân" value={cccd}
                                    onChange={(e) => setCccd(e.target.value)}
                                    onKeyPress={(e) => e.key === 'Enter' && executeSearch(cccd, currentPeriod?.id)}
                                    InputProps={{
                                        startAdornment: <InputAdornment position="start"><AssignmentIndIcon sx={{ color: 'text.secondary', ml: 1 }} /></InputAdornment>,
                                        sx: { borderRadius: "16px", bgcolor: "#f1f5f9", "& fieldset": { border: "none" }, height: "56px" }
                                    }}
                                />
                                <Button variant="contained" disableElevation onClick={() => executeSearch(cccd, currentPeriod?.id)} disabled={loading || !currentPeriod} sx={{ borderRadius: "16px", px: 4, height: "56px", fontWeight: 800, minWidth: "160px" }}>
                                    {loading ? <CircularProgress size={24} color="inherit" /> : "TRA CỨU"}
                                </Button>
                            </Stack>

                            {error && <Zoom in><Alert severity="warning" sx={{ mb: 4, borderRadius: "12px" }}>{error}</Alert></Zoom>}

                            {application && (
                                <Fade in>
                                    <Box>
                                        <Divider sx={{ mb: 4 }}><Chip icon={<InfoIcon />} label="TIẾN ĐỘ HỒ SƠ" color="primary" variant="outlined" /></Divider>

                                        {/* Stepper Timeline */}
                                        <Stepper activeStep={getStatusStep()} alternativeLabel sx={{ mb: 6 }}>
                                            {['Chờ xác minh', 'Xác minh', 'Duyệt hồ sơ', 'Nhận phòng'].map((label) => (
                                                <Step key={label}><StepLabel>{label}</StepLabel></Step>
                                            ))}
                                        </Stepper>

                                        {/* Info Grid */}
                                        <Grid container spacing={3} sx={{ mb: 5 }}>
                                            <Grid item xs={12} sm={6}>
                                                <Card variant="outlined" sx={{ borderRadius: 4, bgcolor: '#f8fafc', borderStyle: 'dashed' }}>
                                                    <CardContent>
                                                        <Typography variant="caption" color="text.secondary" fontWeight={700}>TRẠNG THÁI HIỆN TẠI</Typography>
                                                        <Box sx={{ mt: 1 }}><Chip label={STATUS_LABELS[application.status]} color={STATUS_COLORS[application.status]} sx={{ fontWeight: 800, borderRadius: 1.5 }} /></Box>
                                                    </CardContent>
                                                </Card>
                                            </Grid>
                                            <Grid item xs={12} sm={6}>
                                                <Card variant="outlined" sx={{ borderRadius: 4, bgcolor: '#f8fafc', borderStyle: 'dashed' }}>
                                                    <CardContent>
                                                        <Typography variant="caption" color="text.secondary" fontWeight={700}>MÃ HỒ SƠ / CCCD</Typography>
                                                        <Typography variant="body1" fontWeight={800} sx={{ mt: 0.5 }}>#{application.id} - {application.studentCccd}</Typography>
                                                    </CardContent>
                                                </Card>
                                            </Grid>

                                            {/* Timestamps */}
                                            <Grid item xs={12}>
                                                <Box sx={{ p: 2, bgcolor: '#f1f5f9', borderRadius: 3 }}>
                                                    <Grid container spacing={2}>
                                                        <Grid item xs={6} md={3}>
                                                            <Typography variant="caption" color="text.secondary" display="block">Ngày nộp</Typography>
                                                            <Typography variant="body2" fontWeight={600}>{new Date(application.submittedAt).toLocaleDateString('vi-VN')}</Typography>
                                                        </Grid>
                                                        {application.verifiedAt && (
                                                            <Grid item xs={6} md={3}>
                                                                <Typography variant="caption" color="text.secondary" display="block">Ngày xác minh</Typography>
                                                                <Typography variant="body2" fontWeight={600}>{new Date(application.verifiedAt).toLocaleDateString('vi-VN')}</Typography>
                                                            </Grid>
                                                        )}
                                                        {application.approvedAt && (
                                                            <Grid item xs={6} md={3}>
                                                                <Typography variant="caption" color="text.secondary" display="block">Ngày duyệt</Typography>
                                                                <Typography variant="body2" fontWeight={600}>{new Date(application.approvedAt).toLocaleDateString('vi-VN')}</Typography>
                                                            </Grid>
                                                        )}
                                                        {application.checkinAt && (
                                                            <Grid item xs={6} md={3}>
                                                                <Typography variant="caption" color="text.secondary" display="block">Ngày nhận phòng</Typography>
                                                                <Typography variant="body2" fontWeight={600}>{new Date(application.checkinAt).toLocaleDateString('vi-VN')}</Typography>
                                                            </Grid>
                                                        )}
                                                    </Grid>
                                                </Box>
                                            </Grid>
                                        </Grid>

                                        {/* Documents Section */}
                                        <Typography variant="subtitle1" fontWeight="800" mb={2} display="flex" alignItems="center" gap={1}>
                                            <DescriptionIcon color="primary" fontSize="small" /> HỒ SƠ ĐÃ TẢI LÊN
                                        </Typography>
                                        <Stack spacing={1.5}>
                                            {documents.map((doc) => (
                                                <Paper key={doc.id} variant="outlined" sx={{ p: 2, borderRadius: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                                                        <Avatar sx={{ bgcolor: 'primary.50', color: 'primary.main', width: 32, height: 32 }}><DescriptionIcon sx={{ fontSize: 18 }} /></Avatar>
                                                        <Box>
                                                            <Typography variant="body2" fontWeight="700">{doc.type}</Typography>
                                                            <Typography variant="caption" color="text.secondary" sx={{ maxWidth: 200, display: 'block', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{doc.fileUrl}</Typography>
                                                        </Box>
                                                    </Box>
                                                    <Chip label={doc.status} size="small" variant="outlined" sx={{ fontWeight: 600, fontSize: '0.65rem' }} />
                                                </Paper>
                                            ))}
                                        </Stack>
                                    </Box>
                                </Fade>
                            )}

                            {!application && !loading && !error && (
                                <Box sx={{ textAlign: 'center', py: 6, opacity: 0.3 }}>
                                    <SearchIcon sx={{ fontSize: 64, mb: 2 }} />
                                    <Typography variant="body1" fontWeight={500}>Nhập CCCD để kiểm tra kết quả chi tiết</Typography>
                                </Box>
                            )}
                        </Box>
                    </Paper>
                </Fade>
            </Container>
        </Box>
    );
}