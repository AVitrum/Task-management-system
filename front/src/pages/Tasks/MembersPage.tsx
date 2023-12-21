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
  const { name: teamName } = useParams<{ name: string }>();

  const navigate = useNavigate();

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
        `${backendIp}/api/${teamName}/members/addMember`,
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
      await axios.delete(`${backendIp}/api/${teamName}/members/kick`, {
        data: {
          username: username,
        },

        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      window.location.reload();
      notify("You has kicked user from team");
    } catch (error) {
      notify("Failed to remove user from the team");
    }
  };

  const fetchData = async () => {
    try {
      const res = await axios.get(`${backendIp}/api/${teamName}/members/all`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setMembers(res.data);
    } catch (error: any) {
      notify(error.res.data);
    }
  };
  const fetchData2 = async () => {
    try {
      const res = await axios.get(
        `${backendIp}/api/${teamName}/members/checkPermission`,
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
    fetchData();
    fetchData2();
  }, [token, teamName]);

  return (
    <div className="flex-col justify-start w-72">
      <div className="bg-white pl-2 pr-4 pb-4 rounded-sm shadow-2xl lg:h-[52rem] md:h-[50rem] h-[48rem] overflow-auto custom-scrollbar overscroll-contain">
        <div className="flex items-center my-2">
          <h1 className="font-bold text-lg px-20 bg-white">Members</h1>
          <a className="pr-4" onClick={showModal ? closeModal : openModal}>
            <span className="sr-only"></span>
            <img className="h-6 max-w-none svg-class" src={showModal ? "/minus.svg" : "/plus.svg"} alt="" />
          </a>
        </div>

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
                  <span className="text">Create</span>
                </button>
              </div>
            </form>
          </Modal>
        )}
        {ifManager ? (
          <ul className="bg-slate-400 my-4 py-4 pl-2 pr-10 rounded-md">
            <ul>
              {members.map((member) => (
                <li
                  key={member.id}
                  className="flex justify-between items-center"
                >
                  {member.name} - {member.role}
                  {member.role === "MEMBER" ? (
                    <button onClick={() => removeMember(member.name)}>
                      Remove
                    </button>
                  ) : (
                    <></>
                  )}
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
