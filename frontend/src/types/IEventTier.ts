export type EventTierType = 'GENERAL' | 'FLOOR' | 'VIP';

export default interface IEventTier {
    id?: number
    tier: EventTierType
    price: number
    quantity: number
}