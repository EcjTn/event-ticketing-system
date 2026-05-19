import { useEffect, useState } from "react"
import type IEventBasic from "../types/IEventBasic"
import { getEvents } from "../helpers/events"

function Events() {
    const [events, setEvents] = useState<IEventBasic[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        async function fetchEvents() {
            setLoading(true)
            setError(null)
            try {
                const events = await getEvents();
                setEvents(events);
            } catch (error) {
                setError(error.message)
            } finally {
                setLoading(false)
            }
        }
        fetchEvents()
    }, [])

    if (loading) {
        return <div>Loading...</div>
    }

    if (error) {
        return <div>Error: {error}</div>
    }

    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold mb-8 text-mist">Events</h1>
            <ul>
                {events.map((event) => (
                    <li key={event.id}>{event.name}</li>
                ))}
            </ul>
        </div>
    )
}

export default Events