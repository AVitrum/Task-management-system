import axios from "axios";
import { useEffect, useState } from "react";

interface UserInfo{
    username: string;
    id: number;
    email: string;
    role: string;
}

export default function HomePage() {
    const [userInfo, setUserInfo] = useState<UserInfo>({
      username: "",
      id: 0,
      email: "",
      role: ""
    });

    const profile = async () => {
      const res = await axios.get('http://localhost:8080/api/v1/auth/profile');
      setUserInfo(res.data);
    }

    useEffect(() => {
      profile();
    }, []);

    return(
        <div>
          <>
          {userInfo.id}
          </>
          <br/>
          <>
          {userInfo.username}     
          </>
          <br/>
          <>
          {userInfo.email}
          </>
          <br/>
          <>
          {userInfo.role} 
          </>
        </div>
    );
}