import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";

export default function Layout() {
  return (
    <div className="min-h-screen bg-gradient-to-r from-purple-800 to-pink-800  w-full ">
      <header className="fixed w-full top-0">
        <Header />
      </header>

      <main className="  ">
        <Outlet />
      </main>

      <footer className="fixed bottom-0 w-full  ">
        <Footer />
      </footer>
    </div>
  );
}
