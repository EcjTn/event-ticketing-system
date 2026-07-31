import { useEffect, useState, useCallback } from "react"
import type IEventBasic from "../types/IEventBasic"
import { getEvents } from "../helpers/events"
import EventList from "../components/EventList"
import CreateEventModal from "../components/CreateEventModal"
import { Plus } from "lucide-react"

function Events() {
    const [events, setEvents] = useState<IEventBasic[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [isModalOpen, setIsModalOpen] = useState(false)

    const fetchEvents = useCallback(async () => {
        setLoading(true)
        setError(null)
        try {
            const data = await getEvents()
            setEvents(data)
        } catch (err: any) {
            setError(err.message || "Failed to load events")
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => {
        fetchEvents()
    }, [fetchEvents])

    return (
        <div className="p-8 max-w-7xl mx-auto min-h-screen">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold text-mist">Events</h1>
                <button
                    onClick={() => setIsModalOpen(true)}
                    className="flex items-center gap-2 px-5 py-2.5 bg-mist hover:bg-mist-hover text-navy-bg font-semibold rounded-xl transition-colors shadow-md cursor-pointer"
                >
                    <Plus className="w-5 h-5" />
                    <span>Create Event</span>
                </button>
            </div>

            {loading ? (
                <div className="text-slate-400">Loading events...</div>
            ) : error ? (
                <div className="p-4 bg-red-900/30 border border-red-500/50 text-red-200 rounded-xl">
                    Error: {error}
                </div>
            ) : (
                <EventList events={events} />
            )}

            <CreateEventModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSuccess={() => fetchEvents()}
            />
        </div>
    )
}

export default Events