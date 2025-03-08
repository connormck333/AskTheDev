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

type ScrollContainer = RefObject<HTMLDivElement | null>;

export type {
    Status,
    Chat,
    ScrollContainer
}