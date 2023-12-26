import { useContext } from "react";
import { UserContext } from "../components/UserContext";
import ModalWindow from "../components/Modal/ModalWindow";

export default function AboutPage() {
  const { userInfo } = useContext(UserContext);

  return (
    <div className=" text-white text-xl flex justify-center items-center">
      
        <div className="pt-60">
          <div className=" text-left w-[50rem] text-3xl bg-gradient-to-r from-cyan-700 to-purple-700 border-4 border-black p-4 rounded-md shadow-lg ">
            <p><a className="text-green-500 font-bold "> </a> 
               <a className="text-green-500 font-bold"></a>
              We are a small software development company based in Uzhhorod.
               We have been working in the field of web technologies for a relatively 
               short time, but with each project we are progressing and gaining experience. 
               We will be glad to receive any feedback.
               <br />
               <br />
              Our email is here: 
              <br />
              sasha.g360@gmail.com
           
            </p>
          </div>
        </div>
     
        
    </div>
  );
}
