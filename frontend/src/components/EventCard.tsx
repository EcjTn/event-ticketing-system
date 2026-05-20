import type IEvent from "../types/IEvent"
import { Calendar, Activity, Ticket, MapPin } from "lucide-react"

interface EventCardProps {
    event: IEvent
}

function EventCard({ event }: EventCardProps) {
    return (
        <div>
            <div className="relative h-48 w-full overflow-hidden">
                {event.imageUrl ? (
                    <img
                        src={event.imageUrl}
                        alt={event.name}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                    />
                ) : (
                    <div className="w-full h-full bg-navy-bg flex items-center justify-center text-slate-500">
                        No Image
                    </div>
                )}
            </div>
            <div className="p-6 flex flex-col grow">
                <h3 className="text-xl font-bold text-mist group-hover:text-mist-hover transition-colors">
                    {event.name}
                </h3>
                <div className="flex items-center justify-between text-xs text-slate-500 mb-4">
                    <span className="flex items-center">
                        <MapPin className="w-4 h-4 mr-1" />
                        {event.venue}
                    </span>
                </div>
                <p className="text-sm text-slate-400 mb-4">{event.description}</p>
                <div className="flex flex-col gap-2 mb-4">
                    <span className="flex items-center text-sm text-slate-400">
                        <Calendar className="w-4 h-4 mr-2 text-mist" />
                        {event.date}
                    </span>
                    <span className="flex items-center text-sm text-slate-400">
                        <Activity className="w-4 h-4 mr-2 text-mist" />
                        {event.status}
                    </span>
                    <span className="flex items-center text-sm text-slate-400">
                        <Ticket className="w-4 h-4 mr-2 text-mist" />
                        {event.availableTickets} available
                    </span>
                </div>
                <div className="flex gap-5 justify-center items-center mt-auto pt-4 border-t border-navy-border">
                    {event.tiers.map((tier) => (
                        <button key={tier.id} className="px-6 py-2 bg-cyan-800 hover:bg-cyan-900 text-navy transition-colors rounded-full font-bold text-sm">
                            {tier.tier}: ${tier.price} ({tier.quantity} left)
                        </button>
                    ))}
                </div>
            </div>
        </div>
    )
}

export default EventCard