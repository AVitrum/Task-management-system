import { Outlet } from "react-router-dom";
import Header from "./Header";

export default function Layout(){
    return(
        <div className="relative">
            <header>
                <Header/>
            </header>

            <main className="mt-16 mb-16" >
                <Outlet/>
            </main>

            <footer>TMS2023</footer>
        </div>
    );
}