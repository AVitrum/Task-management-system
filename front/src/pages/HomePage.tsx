import { useContext } from "react";
import { UserContext } from "../components/UserContext";
import ModalWindow from "../components/Modal/ModalWindow";


export default function HomePage() {
  const { userInfo } = useContext(UserContext);

  return (
    <div className=" text-white text-xl flex justify-center items-center">
      
      {userInfo.id === 0 || userInfo.id === undefined ? (
        <div className="pt-72">
          <div className=" text-center w-[50rem] text-3xl bg-gradient-to-r from-cyan-700 to-purple-700 border-4 border-black p-4 rounded-md shadow-lg ">
            <p><a className="text-green-500 font-bold " href="/register">Sign up </a> 
              or <a className="text-green-500 font-bold" href="/login">Sign in</a>
            <br />
            if  you already have an account to get access to our team task
            management system</p>
          </div>
        </div>
      ) : (
        <div className="flex flex-col pt-28 ">
          <div className="w-[35rem] bg-gradient-to-r from-cyan-700 to-purple-700 border-4 border-black p-4 rounded-md shadow-lg ">
            <p>Hello <a className="text-green-500 font-bold">{ userInfo.username} </a>!
            Welcome to the Task Management System
            <a className="text-green-500 font-bold">(TMS) </a>
            for Teams in Waterfall style.Our platform offers effective and
            structured tools for managing projects, allowing your team to
            strictly adhere to the steps of the traditional software development
            model.
            <br /></p>
          </div>
          <div className="w-[35rem]  bg-gradient-to-r from-cyan-700 to-purple-700 border-4 p-4 border-black ml-[45rem] rounded-md shadow-lg ">
            <p>With Waterfall, you gain the ability to control each stage of the
            process, clearly define requirements, make plans, and forecasts,
            contributing to increased productivity and product quality. Our
            tools will help you better allocate tasks, monitor their execution,
            and ensure successful project implementation.
            <br /></p>
          </div>
          <div className="w-[28rem] ml-20 bg-gradient-to-r from-cyan-700 to-purple-700 border-4 border-black pt-4 px-4  rounded-md shadow-lg ">
            <p>Join our Task Management System for Teams in the Waterfall style to
            ensure transparency, accuracy, and timeliness in completing your
            team's tasks.</p>
            <br />
          </div>
        </div>
      )}
    </div>
  );
}
