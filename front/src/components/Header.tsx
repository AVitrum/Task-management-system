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
        const res = await axios.get(`${backendIp}/api/auth/profile`, {
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

        <div className='bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-lg '>
            <nav className="mx-auto flex max-w-7xl items-center justify-between p-6 lg:px-8" aria-label="Global">
                <div className="flex lg:flex-1">
                    <a href="/" className="-m-1.5 -p-1.5">
                        <span className="sr-only">Your Company</span>
                        <img className="h-8 w-auto border-2 border-black rounded-md" src="icon.svg" alt="" />
                    </a>
                </div>

                <div className=" lg:flex lg:gap-x-12">
                    <a href="/" className="text-sm font-semibold leading-6">
                        Home
                    </a>
                    <a href="#" className="text-sm font-semibold leading-6 ">
                        About
                    </a>
                    <a href="#" className="text-sm font-semibold leading-6 ">
                        Help
                    </a>
                </div>
                <div className=" lg:flex lg:flex-1 lg:justify-end lg:gap-x-6" >

                        {userInfo.id === 0 || userInfo.id === undefined ? (
                            <>
                                <Link to="/login" className="text-sm font-semibold leading-6 ">
                                    Log in <span aria-hidden="true">&#10094;</span>
                                </Link>

                                <Link to="/register" className="text-sm font-semibold leading-6 ">
                                    Register <span aria-hidden="true">&#10094;</span>
                                </Link >

                            </>
                        ) : (
                            <button className="text-sm font-semibold leading-6  " onClick={logout}>
                                Logout</button>
                        )}
                    
                </div>
            </nav>

        </div>


    );
}