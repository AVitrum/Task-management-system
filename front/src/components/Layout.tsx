import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";


export default function Layout() {
    return (
        <div className="min-h-screen flex flex-col bg-gradient-to-r from-purple-800 to-pink-800">
            <header >
                <Header />
            </header>

            <main className="w-screen mt-16 mb-16" >
                <Outlet />
                
            </main>

            <footer className=" mt-auto   ">
                <Footer/>
            </footer>
        </div>
    );
}