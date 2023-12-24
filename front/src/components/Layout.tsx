import { Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";

export default function Layout() {
  return (
    <div className="flex flex-col min-h-screen mx-auto" >
      <header className="">
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
