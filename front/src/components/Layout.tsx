import { Outlet } from "react-router-dom";
import Header from "./Header";

export default function Layout(){
    return(
        <div>
            <header>
                <Header/>
            </header>
            <main>
                <Outlet/>
            </main>
        </div>
    );
}