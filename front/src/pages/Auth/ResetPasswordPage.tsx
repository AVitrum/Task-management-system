import axios from "axios";
import { useState, useContext } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";

export default function ResetPage() {
  const [currentPassword, setcurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmationPassword, setconfirmationPassword] = useState("");
  const { token } = useContext(UserContext);

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
      await axios.patch(
        `${backendIp}/api/users/password`,
        {
          currentPassword,
          newPassword,
          confirmationPassword,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      navigate("/");
      window.location.reload();
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  return (
    <div
      className="py-2 mx-10 my-0
         flex flex-col justify-center items-center "
    >
      <div
        className="bg-white border-[4px] border-gray-800 rounded-lg px-8 pt-6 pb-14
           shadow-2xl my-32"
      >
        <form className=" " onSubmit={(e) => onSubmit(e)}>
          <h1 className="text-black tracking-wide text-3xl font-black mb-8 centerForm">
            Reset Password
          </h1>

          <h2 className="textOverInputField">Сurrent Password</h2>
          <input
            type="password"
            placeholder="Type your current password"
            value={currentPassword}
            onChange={(ev) => setcurrentPassword(ev.target.value)}
            className="customInput "
          />

          <h2 className="textOverInputField"> New Password</h2>
          <input
            type="password"
            placeholder="Type your new password"
            value={newPassword}
            onChange={(ev) => setNewPassword(ev.target.value)}
            className="customInput"
          />

          <h2 className="textOverInputField"> Confirm Password</h2>
          <input
            type="password"
            placeholder="Type your confirm password"
            value={confirmationPassword}
            onChange={(ev) => setconfirmationPassword(ev.target.value)}
            className="customInput"
          />

          <div className="centerForm">
            <button className="button-64 mt-6 " type="submit">
              <span className="text">Reset</span>
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
