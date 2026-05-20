import EventCard from '../components/EventCard';
import EventList from '../components/EventList';
import type IEvent from '../types/IEvent';
import type IEventBasic from '../types/IEventBasic';
import type IEventTier from '../types/IEventTier';

const MOCK_EVENTS: IEventBasic[] = [
    {
        id: 1,
        name: 'Tech Conference 2026',
        imageUrl: 'https://dl-cf.splento.com/cdn/2022/01/28/event.jpg',
        venue: 'New York, NY',
        description: 'The best tech conference of 2026. '
    },
    {
        id: 2,
        name: 'Local Music Festival',
        imageUrl: 'https://www.cavinelizabeth.com/wp-content/uploads/2023/03/JCF_CavinElizabeth_4.10.22_43-1500x1125.jpg',
        venue: 'San Francisco, CA',
        description: 'Music for hype and fun'
    },
    {
        id: 3,
        name: 'Startup Pitch Night',
        imageUrl: 'https://images.squarespace-cdn.com/content/v1/604a246ec98ac75732165334/1669222048321-HPLTC2ZYKDK1BSA98HPS/backdrop-ideas-corporate-events.jpg',
        venue: 'Los Angeles, CA',
        description: 'Pitch for funding'
    }
];

const MOCK_EVENT_TIERS: IEventTier[] = [
    {
        id: 1,
        tier: 'GENERAL',
        price: 100,
        quantity: 100,
    },
    {
        id: 2,
        tier: 'FLOOR',
        price: 50,
        quantity: 100,
    },
    {
        id: 3,
        tier: 'VIP',
        price: 200,
        quantity: 100,
    }
]

const MOCK_EVENT: IEvent = {
    id: 1,
    name: 'Tech Conference 2026',
    date: '2026-12-31',
    imageUrl: 'https://dl-cf.splento.com/cdn/2022/01/28/event.jpg',
    venue: 'New York, NY',
    description: 'The best tech conference of 2026. ',
    status: 'PUBLISHED',
    tiers: MOCK_EVENT_TIERS,
    availableTickets: 300,
}


function Playground() {
    return (
        <div className="p-8 min-h-screen">
            <h1 className="text-3xl font-bold mb-8 text-mist">Component Playground</h1>
            <EventList events={MOCK_EVENTS} />

            <div className='mt-25'>
                <h2 className="text-3xl font-bold mb-8 text-mist">Event Card</h2>
                <EventCard event={MOCK_EVENT} />
            </div>

        </div>
    );
}

export default Playground;
