import { Dispatch, ReactElement, SetStateAction } from "react";
import { Chat } from "../utils/interfaces";
import UserType from "../utils/UserType";
import Output from "./Output";
import Question from "./Question";

interface ChatStreamProps {
    stream: [Chat[], Dispatch<SetStateAction<Chat[]>>]
}

export default function ChatStream(props: ChatStreamProps): ReactElement {

    const [stream, setStream] = props.stream;

    return (
        <div className="flex flex-col items-end">
            { stream.map((item: Chat) => (
                item.userType === UserType.AI ?
                    <Output
                        value={item}
                    />
                :
                    <Question
                        value={item}
                    />
            )) }
        </div>
    );
}