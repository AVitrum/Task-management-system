import { useContext, useEffect } from 'react';
import { UserContext } from './UserContext';
import axios from "axios";
import Cookies from "js-cookie";
import { Link, useNavigate } from "react-router-dom";

export default function Header() {

    const { token, setUserInfo, userInfo, setToken } = useContext(UserContext);
    const navigate = useNavigate();

    const profile = async () => {
        const res = await axios.get('http://16.171.232.56:8080/api/v1/auth/profile', {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        setUserInfo(res.data);

    };

    const logout = () => {
        Cookies.remove('userInfo');
        Cookies.remove('token');
        setUserInfo({ id: -1, username: '', email: '', role: '' });
        setToken('');
        navigate('/');
        window.location.reload();
    };

    useEffect(() => {
        profile();
    }, []);

    return (
        
            <div className=' bg-gray-700 text-white shadow-lg'>

                <Link to="/">Home</Link>

                {userInfo.id === -1 ? (
                    <>
                     <Link to="/login">Login</Link>
                     <Link to="/register">Register</Link>
                    </>
                ) : (
                    <button className="" onClick={logout}>Logout</button>
                )}
                   
            </div>
        
    );
}