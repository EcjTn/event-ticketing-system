import EventList from '../components/EventList';
import type IEventBasic from '../types/IEventBasic';

const MOCK_EVENTS: IEventBasic[] = [
    {
        id: 1,
        name: 'Tech Conference 2026',
        imageUrl: 'https://dl-cf.splento.com/cdn/2022/01/28/event.jpg'
    },
    {
        id: 2,
        name: 'Local Music Festival',
        imageUrl: 'https://www.cavinelizabeth.com/wp-content/uploads/2023/03/JCF_CavinElizabeth_4.10.22_43-1500x1125.jpg'
    },
    {
        id: 3,
        name: 'Startup Pitch Night',
        imageUrl: 'https://images.squarespace-cdn.com/content/v1/604a246ec98ac75732165334/1669222048321-HPLTC2ZYKDK1BSA98HPS/backdrop-ideas-corporate-events.jpg'
    }
];

function Playground() {
    return (
        <div className="p-8 min-h-screen">
            <h1 className="text-3xl font-bold mb-8 text-mist">Component Playground</h1>
            <EventList events={MOCK_EVENTS} />
        </div>
    );
}

export default Playground;
