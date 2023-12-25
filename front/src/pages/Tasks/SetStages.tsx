import axios from "axios";
import { useState, useContext } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate,useParams } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";


export default function SetStages() {
  const [requirementsDueDate, setRequirementsDueDate] = useState(new Date());
  const [reviewDueDate, setReviewDueDate] = useState(new Date());
  const [implementationDueDate, setImplementationDueDate] = useState(new Date());
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

  const onSubmitStage = async (e: React.FormEvent) => {
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
      className="flex justify-center bg-slate-600 w-full"
    >
      <div
        className="bg-white px-32 pt-6 
            rounded-sm shadow-2xl my-32"
      >
        <form className=" " onSubmit={(e) => onSubmitStage(e)}>
          <h1 className="text-black tracking-wide text-3xl font-black mb-8 centerForm">
            Set Stages
          </h1>

          <h2 className="textOverInputField">Requirements Date</h2>
          <DatePicker
          selected={requirementsDueDate}  
          onChange={(date: any) => setRequirementsDueDate(date)}
          showTimeSelect
          dateFormat="yyyy-MM-dd HH:mm:ss"
          className="customInput"
        />

          <h2 className="textOverInputField"> Review Date</h2>
          <DatePicker
          selected={reviewDueDate}
          onChange={(date:any) => setReviewDueDate(date)}
          showTimeSelect
          dateFormat="yyyy-MM-dd HH:mm:ss"
          className="customInput"
        />

          <h2 className="textOverInputField"> Implementation Date</h2>
          <DatePicker
          selected={implementationDueDate}
          onChange={(date:any) => setImplementationDueDate(date)}
          showTimeSelect
          dateFormat="yyyy-MM-dd HH:mm:ss"
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
