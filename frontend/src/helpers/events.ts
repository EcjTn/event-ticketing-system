import type IEventBasic from "../types/IEventBasic";
import type { ICreateEventRequest } from "../types/ICreateEventRequest";
import api from "./api";

export async function getEvents(): Promise<IEventBasic[]> {
    const response = await api.get('/events');
    return response.data;
}

export async function createEvent(
    data: ICreateEventRequest,
    image: File
): Promise<{ message: string }> {
    const formData = new FormData();

    const jsonBlob = new Blob([JSON.stringify(data)], { type: 'application/json' });
    formData.append('data', jsonBlob);
    formData.append('image', image);

    const response = await api.post('/events', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });

    return response.data;
}