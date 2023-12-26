import MembersPage from "./MembersPage";
import TasksPage from "./TasksPage";

export default function TasksLayout() {
  return (
    <div className="flex flex-row  text-black   ">
      <MembersPage />
    
      <TasksPage />
      
    </div>
  );
}
