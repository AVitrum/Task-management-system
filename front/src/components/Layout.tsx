import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";


export default function Layout() {
    return (
        <div className="min-h-screen flex flex-col">
            <header >
                <Header />
            </header>

            <main className="centerForm mt-16 mb-16 " >
                <Outlet />
            </main>

            <footer className="centerForm mt-auto mb-4">
                <Footer/>
            </footer>
        </div>
    );
}