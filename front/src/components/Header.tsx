import { useContext, useEffect } from 'react';
import { UserContext } from './UserContext';
import axios from "axios";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";

export default function Header(){

    const {token, setUserInfo, userInfo, setToken } = useContext(UserContext);
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

    return(
       <>
        <button className="btnlog" onClick={logout}>Logout</button>
       </>
    );
}