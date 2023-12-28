import { useContext, useState } from "react";
import { UserContext } from "../components/UserContext";
import { ToastContainer, toast } from "react-toastify";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import backendIp from "../serverconfig";

export default function ProfilePage() {
  const { token, userInfo } = useContext(UserContext);
  const [selectedImage, setSelectedImage] = useState(null);

  const handleImageChange = async (event: any) => {
    const file = event.target.files[0];
    setSelectedImage(file);

    if (!selectedImage) {
      console.error("Please select an image");
      return;
    }
    
    const formData = new FormData();
    formData.append("file", selectedImage);

    const response = await axios.patch(`${backendIp}/api/users/image`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
        Authorization: `Bearer ${token}`,
      },
    });

    
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      if (!selectedImage) {
        console.error("Please select an image");
        return;
      }
      const formData = new FormData();
      formData.append("file", selectedImage);

      const response = await axios.patch(`${backendIp}/api/users/image`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${token}`,
        },
      });

      console.log("Image uploaded successfully!", response.data);
    } catch (error) {
      console.error("Error uploading image: ", error);
    }
  };

  return (
    <div className=" text-black text-xl flex justify-center items-center">
      <div
        className="bg-white border-4 border-gray-800 rounded-lg px-5 pt-8 pb-8
             shadow-2xl mb-64   mt-60"
      >
        <form className="flex flex-col " onSubmit={onSubmit}>
          <h1 className="text-black tracking-wide text-3xl font-black mb-4 centerForm">
            Profile
          </h1>
          <input
            type="file"
            id="fileInput"
            accept="image/*"
            onChange={handleImageChange}
            className=""
          />

          <h2 className="">Username:</h2>
          <h3 className="pb-4">{userInfo.username}</h3>

          <h2 className="">Email</h2>
          <h3 className="">{userInfo.email}</h3>
          <br></br>
          <Link
            to="/reset"
            className="block regLogLinks text-lg hover:text-emerald-400 lg:mr-4 md:mr-2 sm:mr-1"
          >
            Change Password
            <span aria-hidden="true">&nbsp;&#10094;</span>
          </Link>
        </form>
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
      </div>
    </div>
  );
}
