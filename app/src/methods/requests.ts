import { GetParam, Status } from "../utils/interfaces";

// const URL: string = "http://localhost:8080";
const URL: string = "https://www.api.askthedev.io"

async function sendPostRequest(endpoint: string, body: any): Promise<Status> {
    try {
        const authToken: string = await getAuthToken();
        const response = await fetch(URL + endpoint, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });

        let data: any;
        try {
            data = await response.json();
        } catch (error: any) {}

        if (response.status != 200 && response.status != 201) {
            return { success: false, errorMessage: data?.message };
        }

        return { success: true, data: data };

    } catch (error: any) {
        console.log(error);
        return { success: false };
    }
}

async function sendGetRequest(endpoint: string, params: GetParam[]): Promise<Status> {
    try {
        const authToken: string = await getAuthToken();
        const response = await fetch(URL + endpoint + formatParams(params), {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        });

        let data: any;
        try {
            data = await response.json();
        } catch (error: any) {}

        if (response.status != 200) {
            return { success: false, errorMessage: data?.message };
        }

        return { success: true, data: data };

    } catch (error: any) {
        console.log(error)
        return { success: false };
    }
}

async function getAuthToken(): Promise<string> {
    try {
        return (await chrome.storage.local.get("atdAuth")).atdAuth;
    } catch (err) {
        console.log(err);
        return "";
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
    sendGetRequest
}