import axios from "axios";
import { useState, useContext } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate, Link } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";

export default function CreateTeam() {
  const [name, setName] = useState("");

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
      const response = await axios.post(
        `${backendIp}/api/teams/create`,
        {
          name,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      navigate("/showTeam");
      window.location.reload();
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  return (
    <div
      className="bg-white border-8 border-gray-800 rounded-lg px-5 pt-8 pb-8
             shadow-2xl mb-64   mt-60"
    >
      <form className=" " onSubmit={(e) => onSubmit(e)}>
        <h1 className="text-black tracking-wide text-3xl font-black mb-4 centerForm">
          Create Team
        </h1>

        <h2 className="textOverInputField">Team</h2>
        <input
          type="text"
          placeholder="Type name of your team"
          value={name}
          onChange={(ev) => setName(ev.target.value)}
          className="mt-1 w-[17rem] py-3 px-2 border border-gray-300 rounded "
          required 
          maxLength={20}
        />
        <div className="flex justify-center text-gray-400 font-normal text-xs pt-1 pb-3">The name can contain a max 20 characters</div>

        <div className="centerForm">
          <button className="button-64 mt-2 " type="submit">
            <span className="text">Create</span>
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
  );
}
