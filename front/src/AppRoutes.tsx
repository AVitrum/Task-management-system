import { Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import HomePage from "./pages/HomePage";
import RegisterPage from "./pages/RegisterPage";
import LoginPage from "./pages/LoginPage";
import { UserContextProvider } from "./components/UserContext";
import ResetPage from "./pages/ResetPasswordPage";
import RecoveryPage from "./pages/RecoveryPage";



export default function AppRoutes() {
    return (
        <UserContextProvider>
            <Routes>
                <Route path="/" element={<Layout />}>
                    <Route index element={<HomePage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/reset" element={<ResetPage />} />
                    <Route path="/recovery" element={<RecoveryPage />} />
                </Route>

            </Routes>
        </UserContextProvider>
    );
}