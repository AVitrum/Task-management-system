import MembersPage from "./MembersPage";
import TasksPage from "./TasksPage";

export default function TasksLayout() {
  return (
    <div className="grid grid-cols-5  text-black gap-9 pt-20 ">
      <MembersPage />

      <TasksPage />
    </div>
  );
}
