import { lazy, Suspense } from "react";
import { CircularProgress, Box } from "@mui/material";
import PublicLayout from "@/layouts/PublicLayout";

const HomePage = lazy(() => import("@/pages/public/HomePage"));
const RegistrationPage = lazy(() => import("@/pages/public/RegistrationPage"));
const StatusPage = lazy(() => import("@/pages/public/StatusPage"));

const Loading = () => (
    <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "100vh" }}>
        <CircularProgress />
    </Box>
);

const wrap = (Component) => (
    <Suspense fallback={<Loading />}>
        <Component />
    </Suspense>
);

export const publicRoutes = [
    {
        path: "/",
        element: <PublicLayout />,
        children: [
            {
                index: true,
                element: wrap(HomePage),
            },
            {
                path: "register",
                element: wrap(RegistrationPage),
            },
            {
                path: "status",
                element: wrap(StatusPage),
            },
        ],
    },
];