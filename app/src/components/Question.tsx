import { ReactElement } from "react";
import { Chat } from "../utils/interfaces";

interface QuestionProps {
    value: Chat
}

export default function Question(props: QuestionProps): ReactElement {

    const chat: Chat = props.value;

    return (
        <div className="flex justify-end gap-2.5 pb-10 w-full">
            <div className="flex flex-col gap-1 max-w-[500px]">
                <div className="flex items-center justify-end space-x-2 rtl:space-x-reverse">
                    <span className="text-sm font-semibold text-gray-900 dark:text-white">You</span>
                    <span className="text-sm font-normal text-gray-500 dark:text-gray-400">{ new Date(chat.timestamp).toLocaleTimeString("en-GB", {hour: "2-digit", minute: "2-digit", day: "numeric", month: "short"}) }</span>
                </div>
                <div className="flex flex-col leading-1.5 p-4 border-gray-200 bg-gray-100 rounded-s-xl rounded-ee-xl dark:bg-gray-700">
                    <p className="text-sm text-left font-normal text-gray-900 dark:text-white">{ chat.message }</p>
                </div>
            </div>
        </div>
    );
}