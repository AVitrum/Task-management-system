import { useContext } from "react";
import { UserContext } from "../components/UserContext";

export default function HomePage() {

    const {userInfo} = useContext(UserContext);

    return(
        <div>
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