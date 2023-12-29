import axios from "axios";
import { useState, useContext, useEffect, useMemo } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate, Link, useParams } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";
import Modal from "../../components/Modall";
import SetStages from "./SetStages";

interface Member {
  id: string;
  name: string;
  role: string;
  email: string;
}

interface Team {
  id: string;
  name: string;
  members: Member[];
  stage: string | null;
}
interface Task {
  id: string;
  title: string;
  description: string;
  status: string;
  isCompleted: boolean;
  creator: Member;
  performer: Member;
  categories: string[];
  assignmentDate: string;
  changeTime: string;
  comments: TaskComments[];
  histories: TaskHistory[];
}
interface TaskHistory {
  taskId: number;
  id: number;
  version: number;
  title: string;
  description: string;
  status: string;
  changeTime: string;
  message: string;
  user: string;
}
interface TaskComments {
  author: string;
  text: string;
  creationTime: string;
}

export default function HistoryPage() {
  const [task, setTask] = useState<Task>();

  const { token } = useContext(UserContext);

  const { teamid: teamIdUrl, taskid: taskIdUrl } = useParams<{
    teamid?: string;
    taskid?: string;
  }>();

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

  const formatDateTime = (dateTimeString: string) => {
    const dateTime = new Date(dateTimeString);
    const hours = dateTime.getHours().toString().padStart(2, "0");
    const minutes = dateTime.getMinutes().toString().padStart(2, "0");
    const day = dateTime.getDate();
    const month = (dateTime.getMonth() + 1).toString().padStart(2, "0");
    const year = dateTime.getFullYear();
    return `${hours}:${minutes} ${day}/${month}/${year}`;
  };

  const getTaskById = async (taskId: string) => {
    try {
      const res = await axios.get(`${backendIp}/api/${teamIdUrl}/${taskId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log(res.data);
      setTask(res.data);
    } catch (error: any) {
      notify(error.res.data);
    }
  };

  useEffect(() => {
    if (teamIdUrl && taskIdUrl) {
      getTaskById(taskIdUrl);
    }
  }, [token, teamIdUrl, taskIdUrl]);

  return (
    <>
      <div
        className="  mx-[30%]   rounded-sm bg-slate-700 text-white border-black 
        shadow-2xl  shadow-orange-600  h-[52rem] overflow-auto custom-scroll-history   "
      >
        <h1 className="flex justify-center p-2 pt-5 text-3xl"> Activity</h1>
        <ul>
          {task &&
            task.histories.map((history: TaskHistory) => (
              <li
                key={history.id}
                className="bgTasks rounded-md my-3 p-4 mx-16"
              >
                <div>
                  <a className="font-light">Message:</a> {history.message}
                </div>
                <div className="bg-slate-700 p-[0.4px]"> </div>

                <div>Title: {history.title}</div>
                <div className="bg-slate-700 p-[0.4px]"> </div>
                <div>Description: {history.description}</div>

                <div className="flex justify-end">
                  {formatDateTime(history.changeTime)}
                </div>
              </li>
            ))}
        </ul>
      </div>
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
    </>
  );
}
