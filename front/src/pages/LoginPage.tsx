import axios from "axios";
import { useState, useContext } from "react";
import { UserContext } from "../components/UserContext";
import { useNavigate } from "react-router-dom";
import backendIp from "../serverconfig";

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const { setToken } = useContext(UserContext);

    const navigate = useNavigate();

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const res = await axios.post(`${backendIp}/api/auth/authenticate`,
        {
            username,     
            password,
        })
        setToken(res.data.token);
        navigate('/');
        window.location.reload();
    }

    return (

        <form className="centerForm " onSubmit={(e) => onSubmit(e)}>
            <h1 className="text-xl font-semibold mb-4">Login</h1>

            <input type="text"
                placeholder="username"
                value={username}
                onChange={ev => setUsername(ev.target.value)}
                className="customInput" />

            <input type="password"
                placeholder="password"
                value={password}
                onChange={ev => setPassword(ev.target.value)}
                className="customInput" />
            <button className="button-64" type="submit" >
                    <span className="text">Login</span>
            </button>
        </form>

    );


}