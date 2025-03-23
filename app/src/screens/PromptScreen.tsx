import { ReactElement, useEffect, useRef, useState } from "react";
import ScrollContainerContext from "../context/scrollContainerContext";
import Logo from "../components/Logo";
import ChatStream from "../components/ChatStream";
import Input from "../components/Input";
import { Chat, SendPromptResponse, Status, User } from "../utils/interfaces";
import UserType from "../utils/UserType";
import { getPreviousPromptsByPage } from "../methods/prompts/getPreviousPrompts";

interface PromptScreenProps {
    user: User
}

export default function PromptScreen(props: PromptScreenProps): ReactElement {

    const { user } = props;
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

    useEffect(() => {
        getPreviousPrompts();
    }, []);

    async function getPreviousPrompts(): Promise<void> {
        const response: Status = await getPreviousPromptsByPage(user.userId, 0);
        if (!response.success) {
            alert("There was an error retrieving your prompt history.");
            return;
        }

        const prevChats: Chat[] = formatPreviousPrompts(response.data.prompts);

        setChatStream([...chatStream, ...prevChats]);
    }

    function formatPreviousPrompts(prevPrompts: SendPromptResponse[]): Chat[] {
        const chats: Chat[] = [];
        for (let prevPrompt of prevPrompts) {
            const timestamp: number = new Date(prevPrompt.createdAt).getTime();
            chats.push({
                message: prevPrompt.userPrompt,
                timestamp: timestamp,
                userType: UserType.Client
            });

            chats.push({
                message: prevPrompt.openAIResponse,
                timestamp: timestamp,
                userType: UserType.AI,
                showTyping: false
            });
        }

        return chats;
    }

    return (
        <div className="p-[2rem] pb-0">
            <div
                ref={scrollContainer}
                className="overflow-scroll scrollbar-hidden flex flex-col items-center h-[70vh] w-[32rem]"
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
            <>
                <Input
                    user={user}
                    prompt={[prompt, setPrompt]}
                    loading={[loading, setLoading]}
                    chatStream={[chatStream, setChatStream]}
                />
            </>
        </div>
    );
}