import axios from "axios";
import { useState, useContext } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate,useParams } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";
import DatePicker from "react-datepicker";

export default function SetStages() {
  const [requirementsDueDate, setRequirementsDueDate] = useState("");
  const [reviewDueDate, setReviewDueDate] = useState("");
  const [implementationDueDate, setImplementationDueDate] = useState("");
  const { token } = useContext(UserContext);
  const { teamid: teamId } = useParams<{ teamid: string }>();

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
      await axios.put(
        `${backendIp}/api/teams/${teamId}/setStages`,
        {
            requirementsDueDate,
            reviewDueDate,
            implementationDueDate,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      navigate(`/tasksLayout/${teamId}`);
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
        className="bg-white px-8 pt-6 pb-14
            rounded-sm shadow-2xl my-32"
      >
        <form className=" " onSubmit={(e) => onSubmit(e)}>
          <h1 className="text-black tracking-wide text-3xl font-black mb-8 centerForm">
            Reset Password
          </h1>

          <h2 className="textOverInputField">Сurrent Password</h2>
          <input
            type="text"
            placeholder="Type your current password"
            value={requirementsDueDate}
            onChange={(ev) => setRequirementsDueDate(ev.target.value)}
            className="customInput "
          />

          <h2 className="textOverInputField"> New Password</h2>
          <input
            type="text"
            placeholder="Type your new password"
            value={reviewDueDate}
            onChange={(ev) => setReviewDueDate(ev.target.value)}
            className="customInput"
          />

          <h2 className="textOverInputField"> Confirm Password</h2>
          <input
            type="text"
            placeholder="Type your confirm password"
            value={implementationDueDate}
            onChange={(ev) => setImplementationDueDate(ev.target.value)}
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
