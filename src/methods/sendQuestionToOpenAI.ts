import { ChatCompletion } from "openai/resources/index.mjs";
import { Status } from "../utils/interfaces";
import { openai } from "./OpenAi";

async function sendQuestionToOpenAI(prompt: string): Promise<Status> {
    try {
        const completion: ChatCompletion = await openai.chat.completions.create({
            model: "gpt-4o-mini",
            messages: [
                {"role": "system", "content": "You are a software engineer expert. A user will provide you with a question based on software engineering. It is your job to provide an accurate and relevant answer."},
                {"role": "user", "content": "Write an answer to the following question: " + prompt}
            ]
        });

        console.log(completion.choices);

        return { success: true, data: completion.choices[0].message.content }
    } catch (error) {
        console.log(error);
        return { success: false};
    }
}

export {
    sendQuestionToOpenAI
}