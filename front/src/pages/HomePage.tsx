import { useState, useEffect, useContext } from "react";
import axios from "axios";
import { UserContext } from "../components/UserContext";

export default function HomePage() {
const { token, setUserInfo, userInfo, setToken } = useContext(UserContext);  
  const [imageData, setImageData] = useState(null);

  useEffect(() => {
    axios.get(userInfo.imagePath,
     { responseType: 'arraybuffer', headers: {
        Authorization: `Bearer ${token}`,
    }, })
      .then(response => {
        const arrayBufferView = new Uint8Array(response.data);
        const blob = new Blob([arrayBufferView], { type: 'image/jpeg' });
        const urlCreator = window.URL || window.webkitURL;
        const imageUrl = urlCreator.createObjectURL(blob);

        setImageData(imageUrl);
      })
      .catch(error => {
        console.error('Помилка отримання зображення:', error);
      });
  }, []);

  return (
    <div>
      {imageData && (
        <img src={imageData} alt="Зображення" />
      )}
    </div>
  );
}
