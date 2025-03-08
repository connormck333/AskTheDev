import OpenAI from "openai";

const openai: OpenAI = new OpenAI({
    apiKey: import.meta.env.VITE_OPENAI_API_KEY,
    dangerouslyAllowBrowser: true
});

export {
    openai
}