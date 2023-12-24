import React, { ReactNode, MouseEventHandler } from "react";

interface ModalProps {
  children: ReactNode;
  onClose: MouseEventHandler<HTMLAnchorElement>;
  onCloseButton: MouseEventHandler<HTMLButtonElement>;
}

const Modal: React.FC<ModalProps> = ({ children, onClose, onCloseButton }) => {
  return (
    <>
      <div className="modal">
        <div className="overlay"></div>

        <div className="modal-content">
          
            <div className="flex justify-end  close-modal">
              <a onClick={onClose}>
                <span className="sr-only"></span>
                <img
                  className="h-5 max-w-none svg-class"
                  src="/cross.svg"
                  alt=""
                />
              </a>
            </div>
          

          {children}
        </div>
      </div>
    </>
  );
};

export default Modal;
