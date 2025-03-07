import { useState, useEffect, useContext } from "react";
import ScrollContainerContext from "../context/scrollContainerContext";
import { ScrollContainer } from "../utils/interfaces";

const useTypeWriterEffect = (
    text: string,
    speed: number = 50
) => {

    const scrollContainer = useContext<ScrollContainer | null>(ScrollContainerContext);
    const [displayText, setDisplayText] = useState<string>("");

    useEffect(() => {
        if (scrollContainer?.current) {
            scrollContainer.current.scrollTop = scrollContainer.current.scrollHeight;
        }
    }, [displayText]);

    useEffect(() => {
        let i: number = 0;
        const typingInterval = setInterval(() => {
            if (i < text.length) {
                setDisplayText(text.substring(0, i + 1));
                i++
            } else {
                clearInterval(typingInterval);
            }
        }, speed);

        return () => {
            clearInterval(typingInterval);
        }
    }, [text, speed]);

    return displayText;
}

export default useTypeWriterEffect;