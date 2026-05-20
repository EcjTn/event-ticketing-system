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
                        <div className="flex items-center justify-between text-xs text-slate-500 mb-4">
                            <span className="flex items-center">
                                <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                                {event.venue}
                            </span>
                        </div>
                        <p className="text-sm text-slate-400 mb-4">{event.description}</p>
                        <div className="flex justify-end items-center mt-auto pt-4 border-t border-navy-border">
                            <button className="px-6 py-2 bg-cyan-800 hover:bg-cyan-900 text-navy transition-colors rounded-full font-bold text-sm">
                                View Details
                            </button>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    )
}

export default EventList