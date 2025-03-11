import { ReactElement, useRef, useState } from "react";
import ScrollContainerContext from "../../context/scrollContainerContext";
import Logo from "../Logo";
import ChatStream from "../ChatStream";
import Input from "../Input";
import { Chat } from "../../utils/interfaces";
import UserType from "../../utils/UserType";

export default function PromptScreen(): ReactElement {

    const [chatStream, setChatStream] = useState<Chat[]>([{
        message: `
### Welcome to AskTheDev!
Please ask me anything, I am already caught up with your current webpage!
        `,
        userType: UserType.AI,
        timestamp: Date.now()
    }]);
    const [prompt, setPrompt] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
    const scrollContainer = useRef<HTMLDivElement>(null);

    return (
        <div>
            <div
                ref={scrollContainer}
                className="overflow-scroll scrollbar-hidden flex flex-col items-center h-[70vh] min-w-[50vw]"
            >
                <ScrollContainerContext.Provider
                    value={scrollContainer}
                >
                    <Logo />
                    <ChatStream
                        stream={[chatStream, setChatStream]}
                    />
                </ScrollContainerContext.Provider>
            </div>
            <div>
                <Input
                    prompt={[prompt, setPrompt]}
                    loading={[loading, setLoading]}
                    chatStream={[chatStream, setChatStream]}
                />
            </div>
        </div>
    );
}