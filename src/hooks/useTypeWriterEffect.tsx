import { useState, useEffect } from "react";

const useTypeWriterEffect = (text: string, speed: number = 50) => {
    const [displayText, setDisplayText] = useState<string>("");

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