import { RefObject } from "react";
import UserType from "./UserType"
import SubscriptionType from "./SubscriptionType";

interface Status {
    success: boolean,
    data?: any
}

interface Chat {
    message: string
    userType: UserType,
    timestamp: number,
    showTyping?: boolean
}

interface GetParam {
    key: string,
    value: string | number
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
    userId: string
}

interface User {
    userId: string,
    email: string,
    activeSubscription: boolean,
    subscriptionType?: SubscriptionType
}

type ScrollContainer = RefObject<HTMLDivElement | null>;

export type {
    Status,
    Chat,
    GetParam,
    SendPromptBody,
    SendPromptResponse,
    User,
    ScrollContainer
}