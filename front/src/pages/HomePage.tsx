import { useContext } from "react";
import { UserContext } from "../components/UserContext";

export default function HomePage() {

    const {userInfo} = useContext(UserContext);

    return(
        <div className="text-white" >
          <br/>
          <br/>
          <br/>
          <br/>
          <br/>
          Here will be something interesting but later
          <br/>
          <>
          {userInfo.id}
          </>
          <br/>
  
      
          <>
          {userInfo.email}
          </>
          <br/>
          <>
          {userInfo.username}
          </>
          <br/>
          <>
          {userInfo.role}
          
          </>
        </div>
    );
}