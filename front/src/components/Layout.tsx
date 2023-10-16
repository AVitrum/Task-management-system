import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";


export default function Layout() {
    return (
        <div className="flex flex-col min-h-screen bg-gradient-to-r from-purple-800 to-pink-800 fixed w-full top-0">
            <header >
                <Header />
            </header>

            <main className="flex-grow  mt-16 mb-16" >
                <Outlet />
                
            </main>

            <footer className="  ">
                <Footer/>
            </footer>
        </div>
    );
}