// src/routes/UserRoutes.jsx

import { Suspense, lazy } from 'react';
import { CircularProgress, Box } from '@mui/material';

const Dashboard = lazy(() => import('../pages/user/Dashboard'));

const LoadingFallback = () => (
    <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <CircularProgress />
    </Box>
);

export const userRoutes = [
    {
        path: '/dashboard',
        element: (
            <Suspense fallback={<LoadingFallback />}>
                <Dashboard />
            </Suspense>
        ),
    },
];