import type IEventBasic from '../types/IEventBasic';

interface EventListProps {
    events: IEventBasic[];
}

function EventList({ events }: EventListProps) {
    if (!events || events.length === 0) {
        return <div className="text-gray-400 italic">No events found.</div>;
    }

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {events.map((event) => (
                <div key={event.id} className="bg-navy-card border border-navy-border rounded-xl shadow-lg hover:shadow-xl hover:border-mist transition-all duration-300 flex flex-col group cursor-pointer overflow-hidden">
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
                    </div>
                </div>
            ))}
        </div>
    )
}

export default EventList