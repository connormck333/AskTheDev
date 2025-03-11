import { RefObject } from "react";
import UserType from "./UserType"

interface Status {
    success: boolean,
    data?: string
}

interface Chat {
    message: string
    userType: UserType,
    timestamp: number
}

interface GetParam {
    key: string,
    value: string
}

interface SendPromptBody {
    userPrompt: string,
    pageContent: string,
    webUrl: string
}

interface SendPromptResponse {
    id: number,
    userPrompt: string,
    pageContent: string,
    webUrl: string,
    openAIResponse: string,
    createdAt: string,
    userId: number
}

type ScrollContainer = RefObject<HTMLDivElement | null>;

export type {
    Status,
    Chat,
    GetParam,
    SendPromptBody,
    SendPromptResponse,
    ScrollContainer
}