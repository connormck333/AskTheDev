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



export type {
    Status,
    Chat
}