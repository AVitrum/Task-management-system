import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";

export default function RegisterPage() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

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
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await axios.post(`${backendIp}/api/auth/register`, {
        username,
        email,
        password,
      });
      navigate("/login");
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  return (
    <div
      className="py-4 mx-10 my-28
        flex flex-col justify-center items-center "
    >
      <div
        className="bg-white px-8 pt-12 pb-14
           rounded-sm shadow-2xl my-30"
      >
        <form className="" onSubmit={(e) => onSubmit(e)}>
          <h1 className="text-black tracking-wide text-3xl font-black mb-8 centerForm">
            Register
          </h1>

          <h2 className="textOverInputField">Username</h2>

          <input
            type="text"
            placeholder="JoeBiden"
            value={username}
            onChange={(ev) => setUsername(ev.target.value)}
            className="customInput"
          />

          <h2 className="textOverInputField">Email</h2>

          <input
            type="email"
            placeholder="JoeBiden@example.com"
            value={email}
            onChange={(ev) => setEmail(ev.target.value)}
            className="customInput"
          />

          <h2 className="textOverInputField">Password</h2>

          <input
            type="password"
            placeholder="Password must contain 8 character and 1 big letter"
            value={password}
            onChange={(ev) => setPassword(ev.target.value)}
            className="customInput"
          />
          <div className="centerForm">
            <button className="button-64 mt-8" type="submit">
              <span className="text">Register</span>
            </button>
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
