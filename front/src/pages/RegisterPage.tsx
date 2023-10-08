import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import backendIp from "../serverconfig";

export default function RegisterPage() {

    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const navigate = useNavigate();

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await axios.post(`${backendIp}/api/auth/register`,
        {
            username,     
            email,
            password,
        });
        navigate('/login');
    }

    return (
        <div>
            <form className="centerForm"
                onSubmit={(e) => onSubmit(e)}>
                <h1>Register</h1>
                <input type="text"
                    placeholder="username"
                    value={username}
                    onChange={ev => setUsername(ev.target.value)}
                    className="customInput" />
                <input type="email"
                    placeholder="email"
                    value={email}
                    onChange={ev => setEmail(ev.target.value)}
                    className="customInput" />

                <input type="password"
                    placeholder="password"
                    value={password}
                    onChange={ev => setPassword(ev.target.value)}
                    className="customInput" />
                <button className="button-64" type="submit" >
                    <span className="text">Register</span>
                </button>
            </form>

        </div>

    );
}

