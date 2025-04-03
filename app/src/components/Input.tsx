import { ChangeEvent, Dispatch, ReactElement, SetStateAction, useContext, useEffect, useState } from "react";
import { Chat, Model, ScrollContainer, Status, User } from "../utils/interfaces";
import { sendQuestionToOpenAI } from "../methods/prompts/sendQuestionToOpenAI";
import UserType from "../utils/UserType";
import Button from "./Button";
import SubscriptionType from "../utils/SubscriptionType";
import { summariseWebPage } from "../methods/prompts/summariseWebPage";
import ScrollContainerContext from "../context/scrollContainerContext";
import ModelSelector from "./ModelSelector";

interface InputProps {
    user: User,
    chatStream: [Chat[], Dispatch<SetStateAction<Chat[]>>],
    prompt: [string, Dispatch<SetStateAction<string>>],
    loading: [boolean, Dispatch<SetStateAction<boolean>>]
}

const models: Model[] = [
    { name: "GPT-4o mini", description: "A lightweight version of GPT-4o, optimized for speed and efficiency.", id: "gpt-4o-mini" },
    { name: "GPT-4o", description: "The latest OpenAI model offering top-tier performance across multiple tasks.", id: "gpt-4o" },
    { name: "OpenAI o3-mini", description: "A compact and cost-effective AI model designed for quick responses. (Best for coding)", id: "o3-mini" },
];

export default function Input(props: InputProps): ReactElement {

    const { user } = props;
    const [chatStream, setChatStream] = props.chatStream;
    const [prompt, setPrompt] =  props.prompt;
    const [loading, setLoading] = props.loading;
    const [selectedModel, setSelectedModel] = useState<Model>(models[0]);
    const scrollContainer = useContext<ScrollContainer | null>(ScrollContainerContext);

    useEffect(() => {
        (() => {
            if (chatStream[chatStream.length - 1].userType === UserType.Client) {
                console.log("chat streaming");
                if (scrollContainer?.current) {
                    scrollContainer.current.scrollTop = scrollContainer.current.scrollHeight;                    
                }
            }
        })();
    }, [chatStream]);

    async function submitPrompt(): Promise<void> {
        if (loading) return;
        if (prompt.length < 2) {
            alert("Please enter a question.");
            return;
        }

        loadNewPrompt(prompt);

        setLoading(true);

        const response: Status = await sendQuestionToOpenAI(user.userId, prompt, selectedModel.id);

        setLoading(false);

        if (!response || !response.data) {
            alert("There was an error communicating with OpenAI. Please try again later.");
            return;
        }

        setChatStream([...chatStream, {
            message: response.data.prompt.openAIResponse,
            userType: UserType.AI,
            timestamp: Date.now()
        }]);
    }

    async function summarisePage(): Promise<void> {
        if (loading) return;
        if (user.subscriptionType?.valueOf() === SubscriptionType.BASIC.valueOf()) {
            alert("Upgrade to Pro to access this feature.");
            return;
        }

        loadNewPrompt("Summarise this web page.");

        setLoading(true);

        const response: Status = await summariseWebPage(user.userId, selectedModel.id);

        setLoading(false);

        if (!response || !response.data) {
            alert("There was an error communicating with OpenAI. Please try again later.");
            return;
        }

        setChatStream([...chatStream, {
            message: response.data.prompt.openAIResponse,
            userType: UserType.AI,
            timestamp: Date.now()
        }]);
    }

    function loadNewPrompt(promptMessage: string): void {
        const savedStream: Chat[] = chatStream;
        savedStream.push({
            message: promptMessage,
            userType: UserType.Client,
            timestamp: Date.now()
        });

        setChatStream([...savedStream]);
        setPrompt("");
    }

    function onEnterKeyPressed(e: React.KeyboardEvent<HTMLTextAreaElement>): void {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            submitPrompt();
        }
    }

    return (
        <div className="relative w-[32rem] mt-[5]">
            <form
                className="relative w-full min-w-[200px]"
            >
                <textarea
                    rows={2}
                    value={prompt}
                    onKeyDown={onEnterKeyPressed}
                    onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setPrompt(e.target.value)}
                    className="peer h-full min-h-[70px] w-full !resize-none rounded-[7px] border border-blue-gray-200 bg-transparent px-3 py-2.5 font-sans text-sm font-normal text-blue-gray-700 outline outline-0 transition-all placeholder-shown:border placeholder-shown:border-blue-gray-200 placeholder-shown:border-t-blue-gray-200 focus:border-2 focus:border-gray-900 focus:border-t-transparent focus:outline-0 disabled:resize-none disabled:border-0 disabled:bg-blue-gray-50"
                    placeholder=" "></textarea>
                <label
                    className="before:content[' '] after:content[' '] pointer-events-none absolute left-0 -top-1.5 flex h-full w-full select-none text-[11px] font-normal leading-tight text-blue-gray-400 transition-all before:pointer-events-none before:mt-[6.5px] before:mr-1 before:box-border before:block before:h-1.5 before:w-2.5 before:rounded-tl-md before:border-t before:border-l before:border-blue-gray-200 before:transition-all after:pointer-events-none after:mt-[6.5px] after:ml-1 after:box-border after:block after:h-1.5 after:w-2.5 after:flex-grow after:rounded-tr-md after:border-t after:border-r after:border-blue-gray-200 after:transition-all peer-placeholder-shown:text-sm peer-placeholder-shown:leading-[3.75] peer-placeholder-shown:text-blue-gray-500 peer-placeholder-shown:before:border-transparent peer-placeholder-shown:after:border-transparent peer-focus:text-[11px] peer-focus:leading-tight peer-focus:text-gray-900 peer-focus:before:border-t-2 peer-focus:before:border-l-2 peer-focus:before:!border-gray-900 peer-focus:after:border-t-2 peer-focus:after:border-r-2 peer-focus:after:!border-gray-900 peer-disabled:text-transparent peer-disabled:before:border-transparent peer-disabled:after:border-transparent peer-disabled:peer-placeholder-shown:text-blue-gray-500">
                    Your Question
                </label>
            </form>
            <div className="flex w-full justify-between py-1.5">
                <ModelSelector
                    model={[selectedModel, setSelectedModel]}
                    models={models}
                />
                <div className="flex justify-end gap-2">
                    <Button
                        label="Summarise"
                        onClick={summarisePage}
                        backgroundColor="#E4E4E4"
                    />
                    <Button
                        label="Submit"
                        onClick={submitPrompt}
                        backgroundColor="#2b7fff"
                        fontColor="#FFF"
                    />
                </div>
            </div>
        </div>
    );
}
