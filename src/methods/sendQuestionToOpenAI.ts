import { ChatCompletion } from "openai/resources/index.mjs";
import { Status } from "../utils/interfaces";
import { openai } from "./OpenAi";
import { getCurrentTabHTML } from "./getCurrentTabHTML";

async function sendQuestionToOpenAI(prompt: string): Promise<Status> {
    const tabTextContent = await getCurrentTabHTML();
    if (!tabTextContent.success) {
        return { success: false };
    }

    try {
        const completion: ChatCompletion = await openai.chat.completions.create({
            model: "gpt-4o-mini",
            messages: [
                {"role": "system", "content": "You are a software engineer expert. A user will provide you with a question based on the current tab that they have open. Generally, this will be a documentation. You will be provided with the text content from this page. This may contain irrelevant information as it has been parsed from the html of the webpage. You should be able to recognise irrelevant content and ignore it. It is your job to provide an accurate and relevant answer to the user's question using the provided web page text as context."},
                {"role": "user", "content": "Here is the text of the current Chrome tab: " + tabTextContent.data + ". Please provide an answer using the html as context to the following question / prompt: " + prompt}
            ]
        });

        console.log(completion.choices);

        return { success: true, data: completion.choices[0].message.content }
    } catch (error) {
        return { success: false};
    }
}

export {
    sendQuestionToOpenAI
}