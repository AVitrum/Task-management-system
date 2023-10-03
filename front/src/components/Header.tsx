import { useContext, useEffect } from 'react';
import { UserContext } from './UserContext';
import axios from "axios";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import backendIp from '../serverconfig';

export default function Header(){

    const {token, setUserInfo, userInfo, setToken } = useContext(UserContext);
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
        setUserInfo({ id: -1, username: '', email: '', role: '' });
        setToken('');
        navigate('/');
        window.location.reload();
    };

    useEffect(() => {
        profile();
    }, []);

    return(
       <>
        <button className="btnlog" onClick={logout}>Logout</button>
       </>
    );
}