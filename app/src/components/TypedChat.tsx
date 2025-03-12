import { ReactElement, RefObject } from "react";
import useTypeWriterEffect from "../hooks/useTypeWriterEffect";
import Markdown from "react-markdown";
import rehypeRaw from "rehype-raw";

interface TypedChatProps {
    message: string,
    speed: number,
    containerRef: RefObject<HTMLDivElement | null>,
    disabled: boolean
}

export default function TypedChat(props: TypedChatProps): ReactElement {

    const displayText: string = props.disabled ? props.message : useTypeWriterEffect(props.message, props.speed);

    return (
        <div className="text-sm font-normal text-left text-gray-900 dark:text-white markdown">
            <Markdown
                rehypePlugins={[rehypeRaw]}
                children={displayText}
            />
        </div>
    );
}