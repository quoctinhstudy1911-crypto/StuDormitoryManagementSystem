import { useRoutes } from "react-router-dom";
import { publicRoutes } from "./PublicRoutes.jsx";

export default function AppRouter() {
    return useRoutes([
        ...publicRoutes
    ]);
}