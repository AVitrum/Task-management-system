import axios from "axios";
import { useState, useContext } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate, Link } from "react-router-dom";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import backendIp from "../../serverconfig";

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const { setToken } = useContext(UserContext);

    const navigate = useNavigate();

    const notify = (message: string) => {
        toast.error(message, {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
        });
    }

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await axios.post(`${backendIp}/api/auth/authenticate`, {
                username,
                password,
            })
            setToken(response.data.token);

            navigate('/showTeam');
            window.location.reload();

        } catch (error: any) {
            notify(error.response.data);
        }
    }

    return (
        <div className="py-2 mx-10 my-0
         flex flex-col justify-center items-center ">
            <div className="bg-white px-8 pt-8 pb-12
            rounded-sm shadow-2xl my-32">
                <form className=" " onSubmit={(e) => onSubmit(e)}>
                    <h1 className="text-black tracking-wide text-3xl font-black mb-8 centerForm">Login</h1>

                    <h2 className="textOverInputField">Username</h2>
                    <input type="text"
                        placeholder="Type your username or email"
                        value={username}
                        onChange={ev => setUsername(ev.target.value)}
                        className="customInput " />

                    <h2 className="textOverInputField">Password</h2>
                    <input type="password"
                        placeholder="Type your password"
                        value={password}
                        onChange={ev => setPassword(ev.target.value)}
                        className="customInput" />



                    <div className="centerForm">
                        <button className="button-64 mt-8 " type="submit" >
                            <span className="text">Login</span>
                        </button>
                    </div>

                    <div className="centerForm">
                        <Link to="/recovery" className="text-sm tracking-wide pt-2 trtransition-colors hover:font-semibold">
                            Forgot password?
                        </Link>
                    </div>
                </form>
                <ToastContainer
                    position="top-right"
                    autoClose={5000}
                    hideProgressBar={false}
                    newestOnTop={false}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                    theme="colored"
                />
            </div>
        </div>

    );


}