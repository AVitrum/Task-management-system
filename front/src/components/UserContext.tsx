import { createContext, useState, ReactNode } from "react";
import Cookies from "js-cookie";

interface UserContextProps {
  children: ReactNode;
}

interface UserInfo {
  id: number;
  username: string;
  email: string;
  role: "";
}

interface UserContextData {
  userInfo: UserInfo;
  setUserInfo: (newUserInfo: UserInfo) => void;
  token: string;
  setToken: (newToken: string) => void;
}

export const UserContext = createContext<UserContextData>({
  userInfo: {
    id: 0,
    username: "",
    email: "",
    role: "",
  },
  setUserInfo: () => {},
  token: "",
  setToken: () => {},
});

export function UserContextProvider({ children }: UserContextProps) {
  const [userInfo, setUserInfoState] = useState<UserInfo>(
    JSON.parse(Cookies.get("userInfo") || "{}")
  );

  const setUserInfo = (newUserInfo: UserInfo) => {
    Cookies.set("userInfo", JSON.stringify(newUserInfo));
    setUserInfoState(newUserInfo);
  };

  const [token, setTokenState] = useState(Cookies.get("token") || "");

  const setToken = (newToken: string) => {
    Cookies.set("token", newToken);
    setTokenState(newToken);
  };

  return (
    <UserContext.Provider value={{ userInfo, setUserInfo, token, setToken }}>
      {children}
    </UserContext.Provider>
  );
}
