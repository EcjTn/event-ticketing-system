import type IEventTier from "./IEventTier"

export default interface IEvent {
    id: number
    name: string
    description: string
    imageUrl: string
    status: string
    createdAt: string
    tiers: IEventTier[]
    availableTickets: number
}