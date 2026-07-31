import type { EventTierType } from './IEventTier';

export interface ICreateEventTier {
    tier: EventTierType;
    price: number;
    quantity: number;
}

export interface ICreateEventRequest {
    name: string;
    date: string;
    venue: string;
    description: string;
    tiers: ICreateEventTier[];
}
