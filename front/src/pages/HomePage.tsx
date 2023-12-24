import { useContext } from "react";
import { UserContext } from "../components/UserContext";
import ModalWindow from "../components/Modal/ModalWindow";

export default function HomePage() {
  const { userInfo } = useContext(UserContext);

  return (
    <div className="text-white">
      {userInfo.id === 0 || userInfo.id === undefined ? (
        <div>
        
        </div>
      ) : (
        <div className="flex justify-center py-96">
          <>{userInfo.id}</>
          <br/>
          <>{userInfo.email}</>
          <br/>
          <>{userInfo.username}</>
          <br/>
          <>{userInfo.role}</>
          
        </div>
      )}
      Here will be something interesting but later
    </div>
  );
}
