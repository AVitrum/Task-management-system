import { useContext } from "react";
import { UserContext } from "../components/UserContext";

export default function HelpPage() {
  const { userInfo } = useContext(UserContext);

  return (
    <div className=" text-white text-xl flex justify-center items-center">
      
        <div className="pt-60">
          <div className=" text-left w-[55rem] text-3xl bg-gradient-to-r from-cyan-700 to-purple-700 border-4 border-black p-4 rounded-md shadow-lg ">
            <p> If you<a className="text-green-500 font-bold "> don't know </a> 
                something, you can always ask <a className="text-green-500 hover:text-green-300 font-bold" href="https://chat.openai.com/">ChatGPT</a>
               
               <br />
              
           
            </p>
          </div>
        </div>
     
        
    </div>
  );
}
