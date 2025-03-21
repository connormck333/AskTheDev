import { ChangeEvent, Dispatch, ReactElement, SetStateAction } from "react";
import { Chat, Status } from "../utils/interfaces";
import { sendQuestionToOpenAI } from "../methods/prompts/sendQuestionToOpenAI";
import Spinner from "./Spinner";
import UserType from "../utils/UserType";

interface InputProps {
    userId: string,
    chatStream: [Chat[], Dispatch<SetStateAction<Chat[]>>],
    prompt: [string, Dispatch<SetStateAction<string>>],
    loading: [boolean, Dispatch<SetStateAction<boolean>>]
}

export default function Input(props: InputProps): ReactElement {

    const { userId } = props;
    const [chatStream, setChatStream] = props.chatStream;
    const [prompt, setPrompt] =  props.prompt;
    const [loading, setLoading] = props.loading;

    async function submitPrompt(): Promise<void> {
        if (loading) return;
        if (prompt.length < 2) {
            alert("Please enter a question.");
            return;
        }

        const savedStream: Chat[] = chatStream;
        savedStream.push({
            message: prompt,
            userType: UserType.Client,
            timestamp: Date.now()
        });

        setChatStream([...savedStream]);
        setPrompt("");

        setLoading(true);

        const response: Status = await sendQuestionToOpenAI(userId, prompt);

        console.log(response)

        setLoading(false);

        if (!response || !response.data) {
            alert("There was an error communicating with OpenAI. Please try again later.");
            return;
        }

        setChatStream([...savedStream, {
            message: response.data.prompt.openAIResponse,
            userType: UserType.AI,
            timestamp: Date.now()
        }]);
    }

    function closeWindow(): void {
        window.close();
    }

    return (
        <div className="relative w-[32rem] mt-10">
            <div className="relative w-full min-w-[200px]">
                <textarea
                    rows={2}
                    value={prompt}
                    onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setPrompt(e.target.value)}
                    className="peer h-full min-h-[70px] w-full !resize-none  rounded-[7px] border border-blue-gray-200 bg-transparent px-3 py-2.5 font-sans text-sm font-normal text-blue-gray-700 outline outline-0 transition-all placeholder-shown:border placeholder-shown:border-blue-gray-200 placeholder-shown:border-t-blue-gray-200 focus:border-2 focus:border-gray-900 focus:border-t-transparent focus:outline-0 disabled:resize-none disabled:border-0 disabled:bg-blue-gray-50"
                    placeholder=" "></textarea>
                <label
                    className="before:content[' '] after:content[' '] pointer-events-none absolute left-0 -top-1.5 flex h-full w-full select-none text-[11px] font-normal leading-tight text-blue-gray-400 transition-all before:pointer-events-none before:mt-[6.5px] before:mr-1 before:box-border before:block before:h-1.5 before:w-2.5 before:rounded-tl-md before:border-t before:border-l before:border-blue-gray-200 before:transition-all after:pointer-events-none after:mt-[6.5px] after:ml-1 after:box-border after:block after:h-1.5 after:w-2.5 after:flex-grow after:rounded-tr-md after:border-t after:border-r after:border-blue-gray-200 after:transition-all peer-placeholder-shown:text-sm peer-placeholder-shown:leading-[3.75] peer-placeholder-shown:text-blue-gray-500 peer-placeholder-shown:before:border-transparent peer-placeholder-shown:after:border-transparent peer-focus:text-[11px] peer-focus:leading-tight peer-focus:text-gray-900 peer-focus:before:border-t-2 peer-focus:before:border-l-2 peer-focus:before:!border-gray-900 peer-focus:after:border-t-2 peer-focus:after:border-r-2 peer-focus:after:!border-gray-900 peer-disabled:text-transparent peer-disabled:before:border-transparent peer-disabled:after:border-transparent peer-disabled:peer-placeholder-shown:text-blue-gray-500">
                    Your Question
                </label>
            </div>
            <div className="flex w-full justify-end py-1.5">
                <div className="flex gap-2">
                    <button
                        onClick={closeWindow}
                        className="px-4 py-2 font-sans text-xs font-bold text-center text-gray-900 uppercase align-middle transition-all rounded-md select-none hover:bg-gray-900/10 active:bg-gray-900/20 disabled:pointer-events-none disabled:opacity-50 disabled:shadow-none"
                        type="button">
                        Close
                    </button>
                    <button
                        onClick={submitPrompt}
                        className="select-none rounded-md bg-blue-500 w-20 h-8 text-center align-middle flex justify-center font-sans text-xs font-bold uppercase text-white shadow-md shadow-gray-900/10 transition-all hover:shadow-lg hover:shadow-gray-900/20 focus:opacity-[0.85] focus:shadow-none active:opacity-[0.85] active:shadow-none disabled:pointer-events-none disabled:opacity-50 disabled:shadow-none"
                        type="button"
                    >
                        { loading ?
                            <Spinner />
                        : "Submit"}
                    </button>
                </div>
            </div>
        </div>
    );
}
