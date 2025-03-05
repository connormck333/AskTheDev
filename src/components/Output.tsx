import { ReactElement } from "react";
import { Chat } from "../utils/interfaces";
import TypedChat from "./TypedChat";

interface OutputProps {
    value: Chat
}

export default function Output(props: OutputProps): ReactElement {

    const chat: Chat = props.value;

    return (
        <div className="flex items-start gap-2.5 pb-10 w-full">
            <img className="w-8 h-8 rounded-full" src="/icon.png" alt="Jese image" />
            <div className="flex flex-col gap-1 max-w-[500px]">
                <div className="flex items-center space-x-2 rtl:space-x-reverse">
                    <span className="text-sm font-semibold text-gray-900 dark:text-white">AI</span>
                    <span className="text-sm font-normal text-gray-500 dark:text-gray-400">{ new Date(chat.timestamp).toLocaleTimeString("en-GB", {hour: "2-digit", minute: "2-digit", day: "numeric", month: "short"}) }</span>
                </div>
                <div className="flex flex-col leading-1.5 p-4 border-gray-200 bg-gray-100 rounded-e-xl rounded-es-xl dark:bg-gray-700">
                    <TypedChat
                        message={chat.message}
                        speed={0.5}
                    />
                </div>
            </div>
        </div>
    );
}