import React, { ReactNode, MouseEventHandler } from "react";

interface ModalProps {
  children: ReactNode;
  onClose: MouseEventHandler<HTMLButtonElement | HTMLDivElement>;
  onCloseButton: MouseEventHandler<HTMLButtonElement>;
}


const Modal: React.FC<ModalProps> = ({ children, onClose, onCloseButton }) => {
  return (
    <>
    <div className="modal">
      <div className="overlay"></div>

      <div className="modal-content">
        <button className="close-modal" onClick={onClose}>
          CLOSE
        </button>
        {children}
      </div>
    </div></>
  );
};

export default Modal;
