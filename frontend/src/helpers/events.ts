import type IEventBasic from "../types/IEventBasic";
import api from "./api";

export async function getEvents(): Promise<IEventBasic[]> {
    const response = await api.get('/events');
    return response.data;
}