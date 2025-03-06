import { ReactElement } from "react";
import useTypeWriterEffect from "../hooks/useTypeWriterEffect";
import Markdown from "react-markdown";
import rehypeRaw from "rehype-raw";

interface TypedChatProps {
    message: string,
    speed: number
}

export default function TypedChat(props: TypedChatProps): ReactElement {

    const displayText: string = useTypeWriterEffect(props.message, props.speed);

    return (
        <p className="text-sm font-normal text-left text-gray-900 dark:text-white markdown">
            <Markdown
                rehypePlugins={[rehypeRaw]}
            >
                { displayText }
            </Markdown>
        </p>
    );
}