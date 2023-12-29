import axios from "axios";
import { useState, useContext, useEffect, useMemo } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate, Link, useParams } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";
import Modal from "../../components/Modall";
import SetStages from "./SetStages";
import StagePage from "./StagePage";

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

export default function TasksPage() {
  const [members, setMembers] = useState<Member[]>([]);
  const [member, setMember] = useState<Member>();
  const [creator, setCreator] = useState<Member>();
  const [tasks, setTasks] = useState<Task[]>([]);
  const [team, setTeam] = useState<Team[]>([]);
  const [task, setTask] = useState([]);
  const { token } = useContext(UserContext);

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [status, setStatus] = useState("");
  const [assignmentDate, setAssignmentDate] = useState("");
  const [changeTime, setChangeTime] = useState("");
  const [isCompleted, setIsCompleted] = useState(false);

  const [titleChange, setTitleChange] = useState("");
  const [descriptionChange, setDescriptionChange] = useState("");

  const [performer, setPerformer] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [addPerformerModel, setAddPerformerModel] = useState(false);
  const [showDetailsTask, setDetailsTask] = useState(false);
  const [showDetailsTaskForMembers, setShowDetailsTaskForMembers] =
    useState(false);
  const [showModalHistory, setShowHisory] = useState(false);

  const [isPlusClicked, setIsPlusClicked] = useState(false);
  const [ifManager, setIfManager] = useState(false);
  const [ifTaskCompleted, setIfTaskCompleted] = useState(false);
  const [ifInReview, setIfInReview] = useState(false);
  const [ifRemove, setIfRemove] = useState(false);

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
    description: string,
    isCompleted: boolean,
    status: string,
    member: Member,
    assignmentDate: string,
    changeTime: string,
    creator: Member
  ) => {
    navigate(`/tasksLayout/${teamId}/${taskId}`);
    setTaskId(taskId);
    getTaskById(taskId);

    setMember(member);
    setTitleChange(title);
    setStatus(status);
    setDescriptionChange(description);
    setAssignmentDate(assignmentDate);
    setChangeTime(changeTime);
    setCreator(creator);

    if (isCompleted) {
      setIsCompleted(true);
    } else {
      setIsCompleted(false);
    }

    if (status === "ASSIGNED" || status === "UNCOMPLETED") {
      setIfTaskCompleted(true);
    } else setIfTaskCompleted(false);
    if (status === "IN_REVIEW" || status === "OVERDUE") {
      setIfInReview(true);
    } else setIfInReview(false);
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
    isCompleted: boolean,
    status: string,
    member: Member,
    assignmentDate: string,
    changeTime: string,
    creator: Member
  ) => {
    navigate(`/tasksLayout/${teamId}/${taskId}`);
    setTaskId(taskId);
    getTaskById(taskId);

    setMember(member);
    setTitleChange(title);
    setStatus(status);
    setDescriptionChange(description);
    setAssignmentDate(assignmentDate);
    setChangeTime(changeTime);
    setCreator(creator);
    setTitleChange(title);
    setDescriptionChange(description);

    if (isCompleted) {
      setIsCompleted(true);
    } else {
      setIsCompleted(false);
    }
    if (status === "ASSIGNED" || status === "UNCOMPLETED") {
      setIfTaskCompleted(true);
    } else setIfTaskCompleted(false);

    setShowDetailsTaskForMembers(true);
  };

  const closeShowDetailsTaskForMembers = () => {
    setShowDetailsTaskForMembers(false);
    navigate(`/tasksLayout/${teamId}`);
  };

  const confirmDeleting = (f: Function, taskid: string) => {
    if (confirm("Do you want to delete this task?")) f(taskid);
  };

  const handleAddPerformerAndSetTaskId = (taskId: string) => {
    setTaskId(taskId);
    navigate(`/tasksLayout/${teamId}/${taskId}`);
    openAddPerformerModal();
  };

  const changeStatus = async (status: string, taskid: string) => {
    try {
      await axios.put(
        `${backendIp}/api/${teamId}/${taskid}/update`,
        {
          status: status,
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

  const moveTaskToTrash = async (taskId: string) => {
    try {
      await axios.delete(`${backendIp}/api/${teamId}/${taskId}`, {
        data: {
          username: taskId,
        },

        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      getAllTasksInTeam();
      notify("The task has been moved to trash");
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
      setIfRemove(false);
      getAllTasksInTeam();
      notify("The task has been deleted");
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  const getAllTasksInTeam = async () => {
    if (tasks != undefined) {
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
      closeShowDetailsTask();
      setIsCompleted(!isCompleted);
      getAllTasksInTeam();
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  const formatDateTime = (dateTimeString: string) => {
    const dateTime = new Date(dateTimeString);
    dateTime.setHours(dateTime.getHours() + 2);
    const hours = dateTime.getHours().toString().padStart(2, "0");
    const minutes = dateTime.getMinutes().toString().padStart(2, "0");
    const day = dateTime.getDate();
    const month = (dateTime.getMonth() + 1).toString().padStart(2, "0");
    const year = dateTime.getFullYear();
    return `${hours}:${minutes} ${day}/${month}/${year}`;
  };

  const restoreTask = async (taskid: string) => {
    try {
      await axios.put(
        `${backendIp}/api/${teamId}/${taskid}/restore`,
        {

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
          <form className=" px-5" onSubmit={(e) => changeDetailsSubmit(e)}>
            <div className="flex items-center justify-between  ">
              <h1 className="py-1 mr-auto">Title</h1>

              <Link
                to={`/history/${teamId}/${taskId}`}
                className="bg-green-400 hover:bg-green-300 rounded-md px-2 p-1 text-sm mr-2"
              >
                History
              </Link>

              <Link
                to={`/сomment/${teamId}/${taskId}`}
                className="bg-yellow-300 hover:bg-yellow-200 rounded-md px-3 p-1 text-sm  "
              >
                Comments
              </Link>
            </div>
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
            {ifTaskCompleted ? (
              <div className="py-1">
                <label className="">
                  <input
                    type="checkbox"
                    onChange={() => {
                      confirmTask();
                    }}
                    checked={isCompleted}
                    className="mr-1"
                  />
                  <span>Task Completed</span>
                </label>
              </div>
            ) : (
              <></>
            )}
            <div className="centerForm pt-2">
              <button className="button-32 " type="submit">
                <span className="text">Safe Changes</span>
              </button>
            </div>

            <h1>Status</h1>
            <h2 className=" detailsInfo">{status}</h2>
            <h1>Performer</h1>
            <h2 className="detailsInfo">{member?.name}</h2>
            <h1>Creator </h1>
            <h2 className="detailsInfo">{creator?.name}</h2>
            <h1>Assignment date</h1>
            <h2 className="detailsInfo">{formatDateTime(assignmentDate)}</h2>
            <h1>Change time </h1>
            <h2 className="detailsInfo">{formatDateTime(changeTime)}</h2>
          </form>
        </Modal>
      )}
      {showDetailsTaskForMembers && (
        <Modal
          onClose={closeShowDetailsTaskForMembers}
          onCloseButton={closeShowDetailsTaskForMembers}
        >
          <form className=" px-5" onSubmit={(e) => changeDetailsSubmit(e)}>
            <div className="flex items-center justify-between  ">
              <h1 className="py-1 mr-auto">Title</h1>

              <Link
                to={`/history/${teamId}/${taskId}`}
                className="bg-green-400 ml-10 hover:bg-green-300 rounded-md px-2 p-1 text-sm mr-2"
              >
                History
              </Link>

              <Link
                to={`/сomment/${teamId}/${taskId}`}
                className="bg-yellow-300 hover:bg-yellow-200 rounded-md px-3 p-1 text-sm  "
              >
                Comments
              </Link>
            </div>
            <h2 className="  text-white bg-gray-500 rounded-md p-1 px-4 m-2">
              {titleChange}
            </h2>
            <h1 className="py-1 font-bold text-xl">Description</h1>
            <h2 className=" text-white bg-gray-500 rounded-md  p-1  px-4 m-2">
              {descriptionChange}
            </h2>
            <h1>Status</h1>
            <h2 className=" detailsInfo">{status}</h2>
            <h1>Performer</h1>
            <h2 className="detailsInfo">{member?.name}</h2>
            <h1>Creator </h1>
            <h2 className="detailsInfo">{creator?.name}</h2>
            <h1>Assignment date</h1>
            <h2 className="detailsInfo">{formatDateTime(assignmentDate)}</h2>
            <h1>Change time </h1>
            <h2 className="detailsInfo">{formatDateTime(changeTime)}</h2>{" "}
            {ifTaskCompleted ? (
              <div className="py-1">
                <label className="">
                  <input
                    type="checkbox"
                    onChange={() => {
                      confirmTask();
                    }}
                    checked={isCompleted}
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
        <>
          <div className="flex flex-col overflow-auto custom-scroll-tasks">
            <StagePage />
            <div className="flex  text-yellow-50 flex-row gap-[13px] overflow-auto custom-scroll-tasks mb-1 py-1 px-1">
              <div className="flex flex-col ">
                <div className="card bgTasksUp rounded-t-xl ">
                  <div className="flex justify-between items-center ">
                    <h2 className="text-xl font-bold">Pending</h2>
                    {ifManager && (
                      <div className="flex  ">
                        <a
                          className="pr-4"
                          onClick={showModal ? closeModal : openModal}
                        >
                          <span className="sr-only"></span>
                          <img
                            className="h-6 max-w-none svg-class"
                            src="/white_plus.svg"
                            alt=""
                          />
                        </a>
                      </div>
                    )}
                  </div>
                </div>
                <div className="bgColTasks task-colls card  rounded-b-xl  overflow-auto custom-scrollbar">
                  <ul>
                    {tasks.map((task) => (
                      <li key={task.id} className="">
                        {task.status === "PENDING" ? (
                          <>
                            {ifManager ? (
                              <div className="py-2 my-2 pl-2 pr-5  bgTasks   rounded-md">
                                <div className="flex justify-between  rounded">
                                  <h2 className="font-bold text-lg break-all">
                                    {task.title}
                                  </h2>

                                  <button
                                    onClick={() =>
                                      confirmDeleting(moveTaskToTrash, task.id)
                                    }
                                    className="pl-3"
                                  >
                                    <span className="sr-only"></span>
                                    <img
                                      className="h-4 max-w-none svg-class"
                                      src="/white_cross.svg"
                                      alt=""
                                      title="Delete the task"
                                    />
                                  </button>
                                </div>
                                <div className="flex  flex-row justify-between">
                                  <button
                                    onClick={() =>
                                      openShowDetailsTask(
                                        task.id,
                                        task.title,
                                        task.description,
                                        task.isCompleted,
                                        task.status,
                                        task.performer,
                                        task.assignmentDate,

                                        task.changeTime,
                                        task.creator
                                      )
                                    }
                                    className="bgDetails  rounded-md   py-1"
                                  >
                                    <h1>Details</h1>
                                  </button>

                                  <div className="flex justify-center  bg-green-600 hover:bg-green-500 rounded-md  text-sm py-1 pr-1 pl-1">
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
                                        />
                                      </div>
                                    </button>
                                  </div>
                                </div>
                              </div>
                            ) : (
                              <div className="py-2 my-2 pl-2 pr-5 bgTasks  rounded-md">
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
                                        task.isCompleted,
                                        task.status,
                                        task.performer,
                                        task.assignmentDate,

                                        task.changeTime,
                                        task.creator
                                      )
                                    }
                                    className="bgDetails  rounded-md px-8 py-1"
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
                <div className="card  bg-blue-900 rounded-t-xl">
                  <div className="flex justify-between items-center ">
                    <h2 className="text-xl font-bold">Assigned</h2>
                  </div>
                </div>
                <div className="bgColTasks card   task-colls rounded-b-xl">
                  <ul>
                    {tasks.map((task) => (
                      <li key={task.id} className="">
                        {task.status === "ASSIGNED" ||
                        task.status === "UNCOMPLETED" ? (
                          <>
                            {ifManager ? (
                              <div
                                className={`py-2 my-2 pl-2 pr-5 bgTasks ${
                                  task.status === "UNCOMPLETED"
                                    ? "border-2 border-red-600"
                                    : ""
                                } rounded-md`}
                              >
                                <div className="flex justify-between  rounded">
                                  <h2 className="font-bold text-lg break-words">
                                    {task.title}
                                  </h2>

                                  <button
                                    onClick={() =>
                                      confirmDeleting(moveTaskToTrash, task.id)
                                    }
                                  >
                                    <span className="sr-only"></span>
                                    <img
                                      className="h-4 max-w-none svg-class"
                                      src="/white_cross.svg"
                                      alt=""
                                      title="Delete the task"
                                    />
                                  </button>
                                </div>

                                <div className="flex  flex-row   justify-end">
                                  <div className="flex justify-center mr-10 bg-green-600 hover:bg-green-500 rounded-md  text-sm py-1 pr-1 pl-1">
                                    <button
                                      onClick={() =>
                                        handleAddPerformerAndSetTaskId(task.id)
                                      }
                                    >
                                      <div className="flex">
                                        <div> Change Performer</div>
                                        <img
                                          className="h-5 pl-3 max-w-none svg-class"
                                          src="/plus.svg"
                                          alt=""
                                        />
                                      </div>
                                    </button>
                                  </div>
                                  <button
                                    onClick={() =>
                                      openShowDetailsTask(
                                        task.id,
                                        task.title,
                                        task.description,
                                        task.isCompleted,
                                        task.status,
                                        task.performer,
                                        task.assignmentDate,

                                        task.changeTime,
                                        task.creator
                                      )
                                    }
                                    className="bgDetails  rounded-md px-1 py-1"
                                  >
                                    <h1>Details</h1>
                                  </button>
                                </div>
                              </div>
                            ) : (
                              <div
                                className={`py-2 my-2 pl-2 pr-5 bgTasks ${
                                  task.status === "UNCOMPLETED"
                                    ? "border-2 border-red-600"
                                    : ""
                                } rounded-md`}
                              >
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
                                        task.isCompleted,
                                        task.status,
                                        task.performer,
                                        task.assignmentDate,

                                        task.changeTime,
                                        task.creator
                                      )
                                    }
                                    className="bgDetails  rounded-md px-8 py-1"
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
                <div className="card  bg-yellow-600 rounded-t-xl">
                  <div className="flex justify-between items-center ">
                    <h2 className="text-xl font-bold">In Review</h2>
                  </div>
                </div>
                <div className="card  bgColTasks task-colls rounded-b-xl">
                  <ul>
                    {tasks.map((task) => (
                      <li key={task.id} className="">
                        {task.status === "IN_REVIEW" ||
                        task.status === "OVERDUE" ? (
                          <>
                            {ifManager ? (
                              <div
                                className={`py-2 my-2 pl-2 pr-5 bgTasks ${
                                  task.status === "OVERDUE"
                                    ? "border-2 border-red-600"
                                    : ""
                                } rounded-md`}
                              >
                                <div className="flex justify-between  rounded">
                                  <h2 className="font-bold text-lg break-words">
                                    {task.title}
                                  </h2>

                                  <button
                                    onClick={() =>
                                      confirmDeleting(moveTaskToTrash, task.id)
                                    }
                                  >
                                    <span className="sr-only"></span>
                                    <img
                                      className="h-4 max-w-none svg-class"
                                      src="/white_cross.svg"
                                      title="Delete the task"
                                      alt=""
                                    />
                                  </button>
                                </div>
                                <div className="flex flex-row  justify-end">
                                  <div className="flex justify-start">
                                    <button
                                      onClick={() =>
                                        changeStatus("COMPLETED", task.id)
                                      }
                                      className="  rounded-md px-1 "
                                      title="Click to approve"
                                    >
                                      <img
                                        className="h-7 max-w-none svg-class"
                                        src="/tick.svg"
                                        alt=""
                                      />
                                    </button>

                                    <button
                                      onClick={() =>
                                        changeStatus("UNCOMPLETED", task.id)
                                      }
                                      className="  rounded-md px-1 "
                                      title="Click to decline"
                                    >
                                      <img
                                        className="h-8 max-w-none svg-class"
                                        src="/cross_red.svg"
                                        alt=""
                                      />
                                    </button>
                                  </div>
                                  <div className="flex  flex-row justify-end">
                                    <button
                                      onClick={() =>
                                        openShowDetailsTask(
                                          task.id,
                                          task.title,
                                          task.description,
                                          task.isCompleted,
                                          task.status,
                                          task.performer,
                                          task.assignmentDate,

                                          task.changeTime,
                                          task.creator
                                        )
                                      }
                                      className="bgDetails  rounded-md  py-1"
                                    >
                                      <h1>Details</h1>
                                    </button>
                                  </div>
                                </div>
                              </div>
                            ) : (
                              <div
                                className={`py-2 my-2 pl-2 pr-5 bgTasks ${
                                  task.status === "OVERDUE"
                                    ? "border-2 border-red-600"
                                    : ""
                                } rounded-md`}
                              >
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
                                        task.isCompleted,
                                        task.status,
                                        task.performer,
                                        task.assignmentDate,

                                        task.changeTime,
                                        task.creator
                                      )
                                    }
                                    className="bgDetails  rounded-md px-8 py-1"
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
                <div className="card  bg-green-700 rounded-t-xl ">
                  <div className="flex justify-between items-center ">
                    <h2 className="text-xl font-bold">Completed</h2>
                  </div>
                </div>
                <div className="card bgColTasks bg-green-500 task-colls rounded-b-xl">
                  <ul>
                    {tasks.map((task) => (
                      <li key={task.id} className="">
                        {task.status === "COMPLETED" ? (
                          <>
                            {ifManager ? (
                              <div className="py-2 my-2 pl-2 pr-5 bgTasks  rounded-md">
                                <div className="flex justify-between  rounded">
                                  <h2 className="font-bold text-lg break-words">
                                    {task.title}
                                  </h2>

                                 
                                </div>
                                <div className="flex  flex-row justify-between">
                                  <button
                                    onClick={() =>
                                      openShowDetailsTask(
                                        task.id,
                                        task.title,
                                        task.description,
                                        task.isCompleted,
                                        task.status,
                                        task.performer,
                                        task.assignmentDate,

                                        task.changeTime,
                                        task.creator
                                      )
                                    }
                                    className="bgDetails  rounded-md px-1 py-1"
                                  >
                                    <h1>Details</h1>
                                  </button>
                                </div>
                              </div>
                            ) : (
                              <div className="py-2 my-2 pl-2 pr-5 bgTasks  rounded-md">
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
                                        task.isCompleted,
                                        task.status,
                                        task.performer,
                                        task.assignmentDate,

                                        task.changeTime,
                                        task.creator
                                      )
                                    }
                                    className="bgDetails  rounded-md px-8 py-1"
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
                <div className="card  bg-red-900 rounded-t-xl ">
                  <div className="flex justify-between items-center ">
                    <h2 className="text-xl font-bold">Trash</h2>
                  </div>
                </div>
                <div className="card bgColTasks bg-green-500 task-colls rounded-b-xl">
                  <ul>
                    {tasks.map((task) => (
                      <li key={task.id} className="">
                        {task.status === "DELETED" ? (
                          <>
                            {ifManager ? (
                              <div className="py-2 my-2 pl-2 pr-5 bgTasks  rounded-md">
                                <div className="flex justify-between  rounded">
                                  <h2 className="font-bold text-lg break-words">
                                    {task.title}
                                  </h2>

                                  <button
                                    onClick={() =>
                                      confirmDeleting(removeTask, task.id)
                                    }
                                  >
                                    <span className="sr-only"></span>
                                    <img
                                      className="h-4 max-w-none svg-class"
                                      src="/white_cross.svg"
                                      alt=""
                                      title="Delete the task"
                                    />
                                  </button>
                                </div>
                                <div className="flex  flex-row justify-between">
                                  <div className="flex justify-center bg-green-600 hover:bg-green-500 rounded-md  text-sm py-1 pr-1 pl-1">
                                    <button
                                      onClick={() => restoreTask(task.id)}
                                    >
                                      <div className="flex">
                                        <div> Restore</div>
                                        <img
                                          className="h-5 pl-3 max-w-none svg-class"
                                          src="/plus.svg"
                                          alt=""
                                        />
                                      </div>
                                    </button>
                                  </div>
                                  <button
                                    onClick={() =>
                                      openShowDetailsTask(
                                        task.id,
                                        task.title,
                                        task.description,
                                        task.isCompleted,
                                        task.status,
                                        task.performer,
                                        task.assignmentDate,

                                        task.changeTime,
                                        task.creator
                                      )
                                    }
                                    className="bgDetails  rounded-md px-1 py-1"
                                  >
                                    <h1>Details</h1>
                                  </button>
                                </div>
                              </div>
                            ) : (
                              <div className="py-2 my-2 pl-2 pr-5 bgTasks  rounded-md">
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
                                        task.isCompleted,
                                        task.status,
                                        task.performer,
                                        task.assignmentDate,

                                        task.changeTime,
                                        task.creator
                                      )
                                    }
                                    className="bgDetails  rounded-md px-8 py-1"
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
          </div>
        </>
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
