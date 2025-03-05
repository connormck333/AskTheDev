import { ReactElement } from "react";
import useTypeWriterEffect from "../hooks/useTypeWriterEffect";

interface TypedChatProps {
    message: string,
    speed: number
}

export default function TypedChat(props: TypedChatProps): ReactElement {

    const displayText = useTypeWriterEffect(props.message, props.speed);

    return (
        <p className="text-sm font-normal text-left text-gray-900 dark:text-white">{ displayText }</p>
    );
}