import { GetParam, Status } from "../utils/interfaces";

const URL: string = "http://localhost:8080";

async function sendPostRequest(endpoint: string, body: any): Promise<Status> {
    try {
        const response = await fetch(URL + endpoint, {
            method: 'POST',
            credentials: "include",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        });

        if (response.status != 200 && response.status != 201) {
            return { success: false };
        }

        let data: any;
        try {
            data = await response.json();
        } catch (error: any) {}

        return { success: true, data: data };

    } catch (error: any) {
        return { success: false };
    }
}

async function sendGetRequest(endpoint: string, params: GetParam[]): Promise<Status> {
    try {
        const response = await fetch(URL + endpoint + formatParams(params), {
            method: 'GET',
            credentials: "include",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            }
        });

        if (response.status != 200) {
            return { success: false };
        }

        let data: any;
        try {
            data = await response.json();
        } catch (error: any) {}

        return { success: true, data: data };

    } catch (error: any) {
        console.log(error)
        return { success: false };
    }
}

async function sendDeleteRequest(endpoint: string, body: any): Promise<Status> {
    try {
        const response = await fetch(URL + endpoint, {
            method: 'DELETE',
            credentials: "include",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        });

        if (response.status != 200) {
            return { success: false };
        }

        return { success: true };

    } catch (error: any) {
        return { success: false };
    }
}

async function sendPutRequest(endpoint: string, body: any): Promise<Status> {
    try {
        const response = await fetch(URL + endpoint, {
            method: 'PUT',
            credentials: "include",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        });

        if (response.status != 200) {
            return { success: false };
        }

        return { success: true };

    } catch (error: any) {
        return { success: false };
    }
}

function formatParams(params: GetParam[]): string {
    if (params.length === 0) return "";
    
    let query = "?";
    for (let i = 0; i < params.length; i++) {
        const param: GetParam = params[i];
        query += (i !== 0 ? "&" : "") + param.key + "=" + encodeURIComponent(param.value);
    }

    return query;
}

export {
    sendPostRequest,
    sendGetRequest,
    sendDeleteRequest,
    sendPutRequest
}