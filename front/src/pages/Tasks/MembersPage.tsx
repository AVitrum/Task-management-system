import React, { useState, useContext, useEffect } from "react";
import axios from "axios";
import { useLocation, useParams, useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import backendIp from "../../serverconfig";
import Modal from "../../components/Modall";
import { UserContext } from "../../components/UserContext";

interface Member {
  id: string;
  name: string;
  role: string;
  email: string;
}

export default function MembersPage() {
  const [members, setMembers] = useState<Member[]>([]);
  const [ifManager, setIfManager] = useState(false);

  const [showModal, setShowModal] = useState(false);
  const [isPlusClicked, setIsPlusClicked] = useState(false);

  const [username, setName] = useState("");
  const { token } = useContext(UserContext);
  const { teamid: teamId } = useParams<{ teamid: string }>();

  const navigate = useNavigate();

  const openModal = () => {
    setShowModal(true);
    setIsPlusClicked(!isPlusClicked);
  };

  const closeModal = () => {
    setShowModal(false);
    setIsPlusClicked(!isPlusClicked);
  };

  if (showModal) {
    document.body.classList.add("active-modal");
  } else {
    document.body.classList.remove("active-modal");
  }

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
        `${backendIp}/api/${teamId}/members/addMember`,
        {
          username,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setShowModal(false);

      window.location.reload();
    } catch (error: any) {
      notify(error.response.data);
    }
  };

  const removeMember = async (username: string) => {
    try {
      const res = await axios.delete(
        `${backendIp}/api/${teamId}/members/kick`,
        {
          data: {
            username: username,
          },

          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      notify("Team has been deleted");
      setTimeout(() => {
        navigate("/showTeam");
      }, 1000);
      getMembers();
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

  useEffect(() => {
    getMembers();
    findManager();
  }, [token, teamId]);

  return (
    <>
      {showModal && (
        <Modal onClose={closeModal} onCloseButton={closeModal}>
          <form className="centerForm" onSubmit={(e) => onSubmit(e)}>
            <input
              type="text"
              placeholder="Type name of your member or email"
              value={username}
              onChange={(ev) => setName(ev.target.value)}
              className="memInput"
            />

            <div className="centerForm">
              <button className="button-32 " type="submit">
                <span className="text">Add Member</span>
              </button>
            </div>
          </form>
        </Modal>
      )}
      <div className="flex flex-row mr-[5px]">
        <div className="w-60 bg-white border-black  shadow-2xl lg:h-[51.55rem] md:h-[50rem] h-[48rem] overflow-auto custom-scrollbar border-r-4 border-b-4  rounded-md">
          <div className="flex items-center py-2">
            <h1 className="font-bold text-lg px-16 bg-white ">Members</h1>
            {ifManager ? (
              <a className="" onClick={showModal ? closeModal : openModal}>
                <span className="sr-only"></span>
                <img
                  className="h-6 max-w-none svg-class"
                  src={showModal ? "/minus.svg" : "/plus.svg"}
                  alt=""
                />
              </a>
            ) : (
              <></>
            )}
          </div>

          {ifManager ? (
            <ul className="bg-slate-400 mx-4 py-4 px-2  rounded-md">
              <ul>
                {members.map((member) => (
                  <li key={member.id} className=" ">
                    <div className="flex flex-row items-center justify-between">
                      <div className="flex items-center">
                        {member.name}
                        {member.role === "LEADER" ? (
                          <img
                            className="h-6 max-w-none svg-class ml-1"
                            src="/crown.svg"
                            alt=""
                            title="LEADER"
                          />
                        ) : (
                          <img
                            className="h-6 max-w-none svg-class ml-1"
                            src="/member.svg"
                            alt=""
                            title="MEMBER"
                          />
                        )}
                      </div>
                      <button onClick={() => removeMember(member.name)}>
                        <span className="sr-only">Remove</span>
                        <img
                          className="h-4 max-w-none svg-class flex"
                          src="/cross.svg"
                          alt=""
                        />
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            </ul>
          ) : (
            <ul className="bg-slate-400 my-4 py-4 pl-2 pr-10 rounded-md">
              <ul>
                {members.map((member) => (
                  <li key={member.id}>
                    {member.name} - {member.role}
                  </li>
                ))}
              </ul>
            </ul>
          )}
        </div>
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
