import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";

export default function RecoveryPage() {
  const [code, setCode] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmationPassword, setconfirmationPassword] = useState("");
  const [email, setEmail] = useState("");
  const [codeWasSent, setCodeWasSent] = useState(false);

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
    if (codeWasSent) {
      try {
        await axios.patch(`${backendIp}/api/users/password/reset`, {
          email,
          code,
          newPassword,
          confirmationPassword,
        });
        navigate("/login");
        window.location.reload();
      } catch (error: any) {
        notify(error.response.data);
      }
    } else {
      try {
        await axios.get(
          `${backendIp}/api/users/password/recoverycode/${email}`,
          {}
        );

        setCodeWasSent(true);
      } catch (error: any) {
        notify(error.response.data);
      }
    }
  };

  return (
    <div>
      {codeWasSent === false ? (
        <>
          <div className="py-2 mx-10 my-0 flex flex-col justify-center items-center ">
            <div
              className="bg-white border-[4px] border-gray-800 rounded-lg  px-8 pt-8 pb-10 shadow-2xl my-32">
              <form className=" " onSubmit={(e) => onSubmit(e)}>
                <h1 className="text-black tracking-wide text-3xl font-black mb-8 centerForm">
                  Recover password
                </h1>

                <div className="mb-2 ">
                  <h2 className="textOverInputFields ">
                    To recover your password,enter the
                  </h2>
                  <h2 className="textOverInputFields ">
                    e-mail address you provided during
                  </h2>
                  <h2 className="textOverInputFields "> registration</h2>
                </div>

                <input
                  type="email"
                  placeholder="Type your email"
                  value={email}
                  onChange={(ev) => setEmail(ev.target.value)}
                  className="customInput "
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
        </>
      ) : (
        <>
          <div className=" mx-10 flex flex-col justify-center items-center ">
            <div className="bg-white px-8 pt-8 pb-6 rounded-sm shadow-2xl my-32">
              <form className=" " onSubmit={(e) => onSubmit(e)}>
                <h1 className="text-black tracking-wide text-3xl font-black mb-8 centerForm">
                  Reset Password
                </h1>

                <h2 className="textOverInputField">Сode</h2>
                <input
                  type="text"
                  placeholder="Type code from email"
                  value={code}
                  onChange={(ev) => setCode(ev.target.value)}
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
                  <button className="button-64 mt-6 mb-6 " type="submit">
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
        </>
      )}
    </div>
  );
}
