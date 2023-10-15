import { useContext, useEffect } from 'react';
import { UserContext } from './UserContext';
import axios from "axios";
import Cookies from "js-cookie";
import { Link, useNavigate } from "react-router-dom";
import backendIp from '../serverconfig';


export default function Header() {

    const { token, setUserInfo, userInfo, setToken } = useContext(UserContext);
    const navigate = useNavigate();

    const profile = async () => {

    const res = await axios.get(`${backendIp}/api/users/profile`, {

            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        setUserInfo(res.data);
    };

    const logout = () => {
        Cookies.remove('userInfo');
        Cookies.remove('token');
        setUserInfo({ id: 0, username: '', email: '', role: '' });
        setToken('');
        navigate('/');
        window.location.reload();
    };

    useEffect(() => {
        profile();

    }, []);

    return (
        

        <div className='bg-gradient-to-r from-blue-600 to-purple-600 text-white shadow-lg '>
            <nav className=" flex items-center justify-between p-6 lg:px-8" >
                <div className="flex lg:flex-1">
                    <a href="/" className="-m-1.5 -p-1.5">
                        <span className="sr-only">Your Company</span>
                        <img className="h-8 w-auto border-2 border-black rounded-md" src="icon.svg" alt="" />
                    </a>
                </div>

                <div className="ml-12 ">
                    <a href="/" className="linkHomePage ">
                        Home
                    </a>
                    <a href="#" className="linkHomePage">
                        About
                    </a>
                    <a href="#" className="linkHomePage ">
                        Help
                    </a>
                </div>
                <div className=" lg:flex lg:flex-1 lg:justify-end lg:gap-x-6" >

                    {userInfo.id === 0 || userInfo.id === undefined ? (
                        <>
                            <Link to="/login" className="regLogLinks hover:text-green-500  lg:mr-4 md:mr-2 sm:mr-1">
                                Log in <span aria-hidden="true">&#10094;</span>
                            </Link>

                            <Link to="/register" className="regLogLinks hover:text-amber-500  ml-1">
                                Register <span aria-hidden="true">&#10094;</span>
                            </Link >

                        </>
                    ) : (
                        <button className="regLogLinks hover:text-red-500  "
                            onClick={logout}>
                            Logout <span aria-hidden="true">&#10094;</span></button>
                    )}

                </div>
            </nav>

        </div>



    );
}