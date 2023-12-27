import axios from "axios";
import { useState, useContext, useEffect, useMemo } from "react";
import { UserContext } from "../../components/UserContext";
import { useNavigate, Link } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import backendIp from "../../serverconfig";
import CreateTeam from "./CreateTeam";

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

export default function TeamPage() {
  const [teams, setTeams] = useState<Team[]>([]);
  const { token, userInfo } = useContext(UserContext);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
    
      try {
        const response = await axios.get(`${backendIp}/api/teams/findByUser`, {
          headers: {
            Authorization: `Bearer ${token}`,
          }, 
        });
        setTeams(response.data);
      } catch (error) {
        console.error("Error fetching data: ", error);
      }
    
      
    };

    fetchData();
  }, [token]);
  
  
  return (
    <div className="flex ">
      <div className="flex-col justify-start w-60  ">
        <h1 className=" w-full font-bold text-lg px-6 pt-2  bg-white border-black border-r-4">
          List of Teams
        </h1>
        <div
          className=" pl-2 pr-2  pb-5  rounded-sm bg-white border-black border-r-4
                shadow-2xl  h-[49.3rem] overflow-auto custom-scrollbar   "
        >
          <ul>
            {teams.map((team) => (
              <li key={team.id} className="">
                <ul className="bg-slate-400  my-3 py-2 px-2  rounded-md">
                  <h2 className="font-bold text-lg break-words">{team.name}</h2>

                  <Link
                   to={`/tasksLayout/${team.id}`}
                   state={{ members: team.members }}
                    className="bg-green-400 rounded-md px-2 text-sm"
                  >
                    View Details
                  </Link>
                </ul>
              </li>
            ))}
          </ul>
        </div>
      </div>
    <div className="  flex justify-center items-center w-full">
      <CreateTeam/></div>
    </div>
  );
}
