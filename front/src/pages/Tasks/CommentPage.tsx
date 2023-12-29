import axios from "axios";
import { useState, useContext, useEffect } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate, Link, useParams } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";

interface Member {
  id: string;
  name: string;
  role: string;
  email: string;
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
  id: number;
  author: string;
  text: string;
  creationTime: string;
}

export default function CommentPage() {
  const [task, setTask] = useState<Task>();

  const [text, setText] = useState("");

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

      setTask(res.data);
    } catch (error: any) {
      notify(error.res.data);
    }
  };

  const addComment = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await axios.post(
        `${backendIp}/api/${teamIdUrl}/${taskIdUrl}/addComment`,
        {
          text,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      window.location.reload();
    } catch (error: any) {
      notify(error.response.data);
    }
  };
  console.log(task);
  useEffect(() => {
    if (teamIdUrl && taskIdUrl) {
      getTaskById(taskIdUrl);
    }
  }, [token, teamIdUrl, taskIdUrl]);

  return (
    <>
      <div
        className="  mx-[30%]   rounded-sm bg-slate-700 border-black 
          h-[52rem]   "
      >
        <div
          className=" text-white bg-slate-600 flex justify-center  border-gray-800 rounded-sm  pt-6 pb-4
                 "
        >
          <form className=" " onSubmit={(e) => addComment(e)}>
            <h1 className="text-white  tracking-wide text-3xl   centerForm">
              Comments
            </h1>

            <h2 >Comments</h2>
            <textarea
              placeholder="Write a comment"
              value={text}
              onChange={(ev) => setText(ev.target.value)}
              className="text-black mt-1 w-full py-3 px-2 border-2 border-gray-500 rounded-sm "
              rows={4}
              cols={50}
              maxLength={255}
              required
            />

            <div className=" ">
              <button className="bgTasks rounded-md p-2 px-6 ">
                <span className="text">Safe</span>
              </button>
            </div>
          </form>
        </div>
<div className="h-[35rem] overflow-auto custom-scrollbar">
        <ul className="text-white mt-6 ">
          {task &&
            task.comments.map((comment: TaskComments) => (
              <li key={comment.id} className="">
                <div className="  mx-16 flex justify-between">
                  <div>{comment.author}</div>
                  <div className="text-sm font-extrabold">{formatDateTime(comment.creationTime)}</div>
                </div>
                <div className="bgTasks rounded-md mb-4 p-4 mx-16 break-words">
                  {comment.text}
                </div>
              </li>
            ))}
        </ul></div>
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
