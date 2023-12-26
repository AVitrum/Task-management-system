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
  Boolean: boolean;
  creator: Member;
  performer: Member;
  categories: string[];
  assignmentDate: string;
  changeTime: string;
}

export default function TasksPage() {
  const [members, setMembers] = useState<Member[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [team, setTeam] = useState<Team[]>([]);
  const [task, setTask] = useState<Task[]>([]);
  const { token, userInfo } = useContext(UserContext);

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  const [titleChange, setTitleChange] = useState("");
  const [descriptionChange, setDescriptionChange] = useState("");

  const [performer, setPerformer] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [addPerformerModel, setAddPerformerModel] = useState(false);
  const [showDetailsTask, setDetailsTask] = useState(false);
  const [showDetailsTaskForMembers, setShowDetailsTaskForMembers] =
    useState(false);

  const [isPlusClicked, setIsPlusClicked] = useState(false);
  const [ifManager, setIfManager] = useState(false);
  const [ifTaskCompleted, setIfTaskCompleted] = useState(false);

  const { teamid: teamId } = useParams<{ teamid: string }>();
  const [taskId, setTaskId] = useState("");

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

  const openAddPerformerModal = () => {
    setAddPerformerModel(true);
  };

  const closeAddPerformerModal = () => {
    setAddPerformerModel(false);
  };
  const openShowDetailsTask = (
    taskId: string,
    title: string,
    description: string
  ) => {
    navigate(`/tasksLayout/${teamId}/${taskId}`);
    setTaskId(taskId);
    getTaskById(taskId);

    setTitleChange(title);
    setDescriptionChange(description);

    setDetailsTask(true);
  };

  const closeShowDetailsTask = () => {
    setDetailsTask(false);
    navigate(`/tasksLayout/${teamId}`);
  };

  const openShowDetailsTaskForMembers = (
    taskId: string,
    title: string,
    description: string,
    status: string
  ) => {
    navigate(`/tasksLayout/${teamId}/${taskId}`);
    setTaskId(taskId);
    getTaskById(taskId);

    setTitleChange(title);
    setDescriptionChange(description);

    if (status === "ASSIGNED" || status === "UNCOMPLETED") {
      setIfTaskCompleted(true);
    }
    console.log(status);

    setShowDetailsTaskForMembers(true);
  };

  const closeShowDetailsTaskForMembers = () => {
    setShowDetailsTaskForMembers(false);
    navigate(`/tasksLayout/${teamId}`);
  };

  const handleAddPerformerAndSetTaskId = (taskId: string) => {
    setTaskId(taskId);
    openAddPerformerModal();
  };

  if (showModal) {
    document.body.classList.add("active-modal");
  } else {
    document.body.classList.remove("active-modal");
  }

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await axios.post(
        `${backendIp}/api/${teamId}/createTask`,
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

  const removeTask = async (taskId: string) => {
    try {
      await axios.delete(`${backendIp}/api/${teamId}/${taskId}/history`, {
        data: {
          username: taskId,
        },

        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      getAllTasksInTeam();
      notify("You delete task ");
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  const getAllTasksInTeam = async () => {
    try {
      const res = await axios.get(`${backendIp}/api/${teamId}/tasks`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setTasks(res.data);
    } catch (error: any) {
      notify(error.res.data);
    }
  };

  const addPerformerSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await axios.patch(
        `${backendIp}/api/${teamId}/${taskId}/addPerformer`,
        {
          performer,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      window.location.reload();
      getAllTasksInTeam();
    } catch (error: any) {
      notify(error.response.data);
    }
  };
  const getMembers = async () => {
    try {
      const res = await axios.get(`${backendIp}/api/${teamId}/members/all`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setMembers(res.data);
    } catch (error: any) {
      notify(error.res.data);
    }
  };
  const findManager = async () => {
    try {
      const res = await axios.get(
        `${backendIp}/api/${teamId}/members/checkPermission`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setIfManager(res.data);
    } catch (error: any) {
      notify(error.res.data);
    }
  };

  const getTeamById = async () => {
    try {
      const res = await axios.get(`${backendIp}/api/teams/${teamId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setTeam(res.data);
    } catch (error: any) {
      notify(error.res.data);
    }
  };
  const getTaskById = async (taskId: string) => {
    try {
      const res = await axios.get(`${backendIp}/api/${teamId}/${taskId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setTask(res.data);
    } catch (error: any) {
      notify(error.res.data);
    }
  };

  const changeDetailsSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await axios.put(
        `${backendIp}/api/${teamId}/${taskId}/update`,
        {
          title: titleChange,
          description: descriptionChange,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      getAllTasksInTeam();
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  const confirmTask = async () => {
    try {
      await axios.patch(
        `${backendIp}/api/${teamId}/${taskId}/confirmTask`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  useEffect(() => {
    getMembers();
    findManager();
    getAllTasksInTeam();
    getTeamById();
  }, [token, teamId, taskId]);

  return (
    <>
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

            <div className="centerForm pt-2">
              <button className="button-32 " type="submit">
                <span className="text">Create Task</span>
              </button>
            </div>
          </form>
        </Modal>
      )}
      {addPerformerModel && (
        <Modal
          onClose={closeAddPerformerModal}
          onCloseButton={closeAddPerformerModal}
        >
          <form className="centerForm" onSubmit={(e) => addPerformerSubmit(e)}>
            <input
              type="text"
              placeholder="Type username of performer"
              value={performer}
              onChange={(ev) => setPerformer(ev.target.value)}
              className="memInput"
            />

            <div className="centerForm pt-2">
              <button className="button-32 " type="submit">
                <span className="text">Add Performer</span>
              </button>
            </div>
          </form>
        </Modal>
      )}
      {showDetailsTask && (
        <Modal
          onClose={closeShowDetailsTask}
          onCloseButton={closeShowDetailsTask}
        >
          <form
            className="centerForm px-5"
            onSubmit={(e) => changeDetailsSubmit(e)}
          >
            <h1 className="py-1">Title</h1>
            <input
              type="text"
              value={titleChange}
              onChange={(ev) => setTitleChange(ev.target.value)}
              className="memInput "
            />
            <h1 className="py-1">Description</h1>
            <textarea
              rows={4}
              cols={50}
              value={descriptionChange}
              maxLength={255}
              onChange={(ev) => setDescriptionChange(ev.target.value)}
              className="memInput"
            />

            <div className="py-1">
              <label className="">
                <input
                  type="checkbox"
                  onChange={() => {
                    confirmTask();
                  }}
                  className="mr-1"
                />
                <span>Task Completed</span>
              </label>
            </div>

            <div className="centerForm pt-2">
              <button className="button-32 " type="submit">
                <span className="text">Safe Changes</span>
              </button>
            </div>
          </form>
        </Modal>
      )}
      {showDetailsTaskForMembers && (
        <Modal
          onClose={closeShowDetailsTaskForMembers}
          onCloseButton={closeShowDetailsTaskForMembers}
        >
          <form
            className="centerForm px-5"
            onSubmit={(e) => changeDetailsSubmit(e)}
          >
            <h1 className="py-1 font-bold text-xl">Title</h1>
            <h2 className=" bg-gray-400 rounded-md p-1 px-4 m-2">
              {titleChange}
            </h2>

            <h1 className="py-1 font-bold text-xl">Description</h1>
            <h2 className=" bg-gray-400 rounded-md  p-1  px-4 m-2">
              {descriptionChange}
            </h2>
            {ifTaskCompleted ? (
              <div className="py-1">
                <label className="">
                  <input
                    type="checkbox"
                    onChange={() => {
                      confirmTask();
                    }}
                    className="mr-1"
                  />
                  <span>Task Completed</span>
                </label>
              </div>
            ) : (
              <></>
            )}
          </form>
        </Modal>
      )}

      {team && team.stage === null ? (
        <>
          <SetStages />
        </>
      ) : (
        <div className="flex flex-row gap-[20px] overflow-auto custom-scroll-tasks mb-1 py-1">
          <div className="flex flex-col ">
            <div className="card  bg-gray-500 rounded-t-xl ">
              <div className="flex justify-between items-center ">
                <h2 className="text-xl font-bold">Pending</h2>
                {ifManager && (
                  <div className="flex  my-2">
                    <a
                      className="pr-4"
                      onClick={showModal ? closeModal : openModal}
                    >
                      <span className="sr-only"></span>
                      <img
                        className="h-6 max-w-none svg-class"
                        src={showModal ? "/minus.svg" : "/plus.svg"}
                        alt=""
                      />
                    </a>
                  </div>
                )}
              </div>
            </div>
            <div className={`card bg-gray-400 rounded-b-xl  overflow-auto custom-scrollbar ${ifManager ? 'h-[46.2rem]' : 'h-[46.8rem]'}`}>
              <ul>
                {tasks.map((task) => (
                  <li key={task.id} className="">
                    {task.status === "PENDING" ? (
                      <>
                        {ifManager ? (
                          <div className="py-2 my-2 pl-2 pr-5  bg-orange-400    rounded-md">
                            <div className="flex justify-between  rounded">
                              <h2 className="font-bold text-lg break-words">
                                {task.title}
                              </h2>

                              <button onClick={() => removeTask(task.id)}>
                                <span className="sr-only"></span>
                                <img
                                  className="h-4 max-w-none svg-class"
                                  src="/cross.svg"
                                  alt=""
                                />
                              </button>
                            </div>
                            <div className="flex  flex-row justify-between">
                              <button
                                onClick={() =>
                                  openShowDetailsTask(
                                    task.id,
                                    task.title,
                                    task.description
                                  )
                                }
                                className="bg-purple-500 hover:bg-purple-400  rounded-md px-1 py-1"
                              >
                                <h1>Details</h1>
                              </button>

                              <div className="flex justify-center  bg-green-400 hover:bg-green-500 rounded-md  text-sm py-1 pr-1 pl-1">
                                <button
                                  onClick={() =>
                                    handleAddPerformerAndSetTaskId(task.id)
                                  }
                                >
                                  <div className="flex">
                                    <div> Add Performer</div>
                                    <img
                                      className="h-5 pl-3 max-w-none svg-class"
                                      src="/plus.svg"
                                      alt=""
                                    />{" "}
                                  </div>
                                </button>
                              </div>
                            </div>
                          </div>
                        ) : (
                          <div className="py-2 my-2 pl-2 pr-5 bg-orange-400  rounded-md">
                            <div className="flex justify-between  rounded">
                              <h2 className="font-bold text-lg break-words">
                                {task.title}
                              </h2>
                            </div>
                            <div className="flex  flex-row justify-end">
                              <button
                                onClick={() =>
                                  openShowDetailsTaskForMembers(
                                    task.id,
                                    task.title,
                                    task.description,
                                    task.status
                                  )
                                }
                                className="bg-purple-500 hover:bg-purple-400  rounded-md px-8 py-1"
                              >
                                <h1>Details</h1>
                              </button>
                            </div>
                          </div>
                        )}
                      </>
                    ) : (
                      <></>
                    )}
                  </li>
                ))}
              </ul>
            </div>
          </div>
          <div className="flex flex-col ">
            <div className="card  bg-blue-500 rounded-t-xl">
              <div className="flex justify-between items-center ">
                <h2 className="text-xl font-bold">Assigned</h2>
              </div>
            </div>
            <div className="card  bg-blue-600 task-colls rounded-b-xl">
              <ul>
                {tasks.map((task) => (
                  <li key={task.id} className="">
                    {task.status === "ASSIGNED" ||
                    task.status === "UNCOMPLETED" ? (
                      <>
                        {ifManager ? (
                          <div className="py-2 my-2 pl-2 pr-5 bg-orange-400  rounded-md">
                            <div className="flex justify-between  rounded">
                              <h2 className="font-bold text-lg break-words">
                                {task.title}
                              </h2>

                              <button onClick={() => removeTask(task.id)}>
                                <span className="sr-only"></span>
                                <img
                                  className="h-4 max-w-none svg-class"
                                  src="/cross.svg"
                                  alt=""
                                />
                              </button>
                            </div>
                            <div className="flex  flex-row justify-between">
                              <button
                                onClick={() =>
                                  openShowDetailsTask(
                                    task.id,
                                    task.title,
                                    task.description
                                  )
                                }
                                className="bg-purple-500 hover:bg-purple-400  rounded-md px-1 py-1"
                              >
                                <h1>Details</h1>
                              </button>
                            </div>
                          </div>
                        ) : (
                          <div className="py-2 my-2 pl-2 pr-5 bg-orange-400  rounded-md">
                            <div className="flex justify-between  rounded">
                              <h2 className="font-bold text-lg break-words">
                                {task.title}
                              </h2>
                            </div>
                            <div className="flex  flex-row justify-end">
                              <button
                                onClick={() =>
                                  openShowDetailsTaskForMembers(
                                    task.id,
                                    task.title,
                                    task.description,
                                    task.status
                                  )
                                }
                                className="bg-purple-500 hover:bg-purple-400  rounded-md px-8 py-1"
                              >
                                <h1>Details</h1>
                              </button>
                            </div>
                          </div>
                        )}
                      </>
                    ) : (
                      <></>
                    )}
                  </li>
                ))}
              </ul>
            </div>
          </div>
          <div className="flex flex-col ">
            <div className="card  bg-yellow-500 rounded-t-xl">
              <div className="flex justify-between items-center ">
                <h2 className="text-xl font-bold">In Review</h2>
              </div>
            </div>
            <div className="card  bg-yellow-400 task-colls rounded-b-xl">
              <ul>
                {tasks.map((task) => (
                  <li key={task.id} className="">
                    {task.status === "IN_REVIEW" ||
                    task.status === "OVERDUE" ? (
                      <>
                        {ifManager ? (
                          <div className="py-2 my-2 pl-2 pr-5 bg-orange-400  rounded-md">
                            <div className="flex justify-between  rounded">
                              <h2 className="font-bold text-lg break-words">
                                {task.title}
                              </h2>

                              <button onClick={() => removeTask(task.id)}>
                                <span className="sr-only"></span>
                                <img
                                  className="h-4 max-w-none svg-class"
                                  src="/cross.svg"
                                  alt=""
                                />
                              </button>
                            </div>
                            <div className="flex  flex-row justify-between">
                              <button
                                onClick={() =>
                                  openShowDetailsTask(
                                    task.id,
                                    task.title,
                                    task.description
                                  )
                                }
                                className="bg-purple-500 hover:bg-purple-400  rounded-md px-1 py-1"
                              >
                                <h1>Details</h1>
                              </button>
                            </div>
                          </div>
                        ) : (
                          <div className="py-2 my-2 pl-2 pr-5 bg-orange-400  rounded-md">
                            <div className="flex justify-between  rounded">
                              <h2 className="font-bold text-lg break-words">
                                {task.title}
                              </h2>
                            </div>
                            <div className="flex  flex-row justify-end">
                              <button
                                onClick={() =>
                                  openShowDetailsTaskForMembers(
                                    task.id,
                                    task.title,
                                    task.description,
                                    task.status
                                  )
                                }
                                className="bg-purple-500 hover:bg-purple-400  rounded-md px-8 py-1"
                              >
                                <h1>Details</h1>
                              </button>
                            </div>
                          </div>
                        )}
                      </>
                    ) : (
                      <></>
                    )}
                  </li>
                ))}
              </ul>
            </div>
          </div>
          <div className="flex flex-col ">
            <div className="card  bg-green-600 rounded-t-xl ">
              <div className="flex justify-between items-center ">
                <h2 className="text-xl font-bold">Completed</h2>
              </div>
            </div>
            <div className="card  bg-green-500 task-colls rounded-b-xl">
              <ul>
                {tasks.map((task) => (
                  <li key={task.id} className="">
                    {task.status === "COMPLETED" ? (
                      <>
                        {ifManager ? (
                          <div className="py-2 my-2 pl-2 pr-5 bg-orange-400  rounded-md">
                            <div className="flex justify-between  rounded">
                              <h2 className="font-bold text-lg break-words">
                                {task.title}
                              </h2>

                              <button onClick={() => removeTask(task.id)}>
                                <span className="sr-only"></span>
                                <img
                                  className="h-4 max-w-none svg-class"
                                  src="/cross.svg"
                                  alt=""
                                />
                              </button>
                            </div>
                            <div className="flex  flex-row justify-between">
                              <button
                                onClick={() =>
                                  openShowDetailsTask(
                                    task.id,
                                    task.title,
                                    task.description
                                  )
                                }
                                className="bg-purple-500 hover:bg-purple-400  rounded-md px-1 py-1"
                              >
                                <h1>Details</h1>
                              </button>
                            </div>
                          </div>
                        ) : (
                          <div className="py-2 my-2 pl-2 pr-5 bg-orange-400  rounded-md">
                            <div className="flex justify-between  rounded">
                              <h2 className="font-bold text-lg break-words">
                                {task.title}
                              </h2>
                            </div>
                            <div className="flex  flex-row justify-end">
                              <button
                                onClick={() =>
                                  openShowDetailsTaskForMembers(
                                    task.id,
                                    task.title,
                                    task.description,
                                    task.status
                                  )
                                }
                                className="bg-purple-500 hover:bg-purple-400  rounded-md px-8 py-1"
                              >
                                <h1>Details</h1>
                              </button>
                            </div>
                          </div>
                        )}
                      </>
                    ) : (
                      <></>
                    )}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      )}

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
