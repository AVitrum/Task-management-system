import axios from "axios";
import { useState, useContext, useEffect, useMemo } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate, Link, useParams } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";
import Modal from "../../components/Modall";

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
}
interface Task {
  id: string;
  title: string;
  description: string;
  status: string;
  Boolean: boolean;
  creator: Member;
  performer: Member;
  categories: string[];
  assignmentDate: string;
  changeTime: string;
}

export default function TasksPage() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const { token, userInfo } = useContext(UserContext);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [isPlusClicked, setIsPlusClicked] = useState(false);
  const { name: teamName } = useParams<{ name: string }>();

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

  const openModal = () => {
    setShowModal(true);
    setIsPlusClicked(!isPlusClicked);
  };

  const closeModal = () => {
    setShowModal(false);
    setIsPlusClicked(!isPlusClicked);
  };
  
  if(showModal) {
    document.body.classList.add('active-modal')
  } else {
    document.body.classList.remove('active-modal')
  }

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await axios.post(
        `${backendIp}/api/${teamName}/createTask`,
        {
          title,
          description,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      window.location.reload();
      setTasks(response.data);
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  const getTask = async () => {
    try {
      const res = await axios.get(`${backendIp}/api/${teamName}/tasks`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setTasks(res.data);
    } catch (error: any) {
      notify(error.res.data);
    }
  };

  useEffect(() => {
    getTask();
  }, [token, teamName]);

  return (
    <>
      <div className="card bg-gray-400 ">
        <div className="flex justify-between items-center">
          <h2 className="text-xl font-bold">Pending</h2>
          <div className="flex items-center my-2">
            <a className="pr-4" onClick={showModal ? closeModal : openModal}>
              <span className="sr-only"></span>
              <img
                className="h-6 max-w-none svg-class"
                src={showModal ? "/minus.svg" : "/plus.svg"}
                alt=""
              />
            </a>
          </div>
        </div>
        {showModal && (
                  <Modal onClose={closeModal} onCloseButton={closeModal}>
                    <form className="centerForm" onSubmit={(e) => onSubmit(e)}>
                      <input
                        type="text"
                        placeholder="Type title of new task"
                        value={title}
                        onChange={(ev) => setTitle(ev.target.value)}
                        className="memInput"
                      />
                      <input
                        type="text"
                        placeholder="Type description to new task"
                        value={description}
                        onChange={(ev) => setDescription(ev.target.value)}
                        className="memInput"
                      />

                      <div className="centerForm">
                        <button className="button-32 " type="submit">
                          <span className="text">Create</span>
                        </button>
                      </div>
                    </form>
                  </Modal>
                )}
        <ul className=" my-4 py-4 pl-2 pr-5 rounded-md">
          <ul>
            {tasks.map((task) => (
              <li key={task.id}>
                
                {task.status === "PENDING" ? (
                  <>
                    <div className=" bg-orange-400  my-4  py-4 pl-2 pr-5 rounded-md ">
                      <h2 className="font-bold text-lg break-words">{task.title}</h2>
                    </div>
                  </>
                ) : (
                  <></>
                )}
              </li>
            ))}
          </ul>
        </ul>
      </div>

      <div className="card  bg-blue-600">
        <h2 className="text-xl font-bold">Assigned</h2>
        <div>yes</div>
        <div>yes</div>
        <div>yes</div>
      </div>

      <div className="card bg-yellow-400 ">
        <h2 className="text-xl font-bold">In Review</h2>
      </div>

      <div className="card bg-green-500">
        <h2 className="text-xl font-bold">Completed</h2>
      </div>
    </>
  );
}
