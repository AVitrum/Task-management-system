import axios from "axios";
import { useState, useContext, useEffect, useMemo } from "react";
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

interface Team {
  id: string;
  name: string;
  members: Member[];
  stage: string | null;
  stageDueDate: string | null;
}



export default function StagePage() {
  const [team, setTeam] = useState<Team>();

  const { token } = useContext(UserContext);

  const { teamid: teamIdUrl } = useParams<{teamid?: string;}>();

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

  const getTeamById = async () => {
    try {
      const res = await axios.get(`${backendIp}/api/teams/${teamIdUrl}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setTeam(res.data);
    } catch (error: any) {
      notify(error.res.data);
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
  useEffect(() => {
    if (teamIdUrl) {
      getTeamById();
    }
  }, [token, teamIdUrl]);

  return (
    <>
      
        <h1 className="flex bgStage text-white shadow-sm justify-center p-2 pt-2 rounded-none text-2xl">Current Stage — {team?.stage}  — Until: {formatDateTime(team?.stageDueDate)} </h1>
        
      
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
