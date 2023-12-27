import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";

export default function Layout() {
  return (
    <div className="flex flex-col h-screen">
      <header className="bg-gradient-to-r from-blue-600 to-purple-600 text-white shadow-lg  ">
        <Header />
      </header>

      <main className="  ">
        <Outlet />
      </main>

      <footer className="   mt-auto ">
        <Footer />
      </footer>
    </div>
  );
}
