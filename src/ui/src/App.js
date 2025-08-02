import React, { useState } from 'react';

const API_BASE_URL = "http://localhost:8080/api_war_exploded";
//const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

function App() {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [eventName, setEventName] = useState('');

    // Use 'get' method to handle "search" requests.
    const getEvent = async () => {
        setLoading(true);
        setData(null);
        try {
            const res = await fetch(
                `${API_BASE_URL}/getEvent?name=${eventName}`,
                // `http://localhost:8080/api_war_exploded/getEvent?name=${eventName}`,
            );
            if (res.status > 299) {
                setData(`woops! bad response status ${res.status} from API`);
                return;
            }
            setData(await res.text());
        } catch (e) {
            setData(`woops! an error occurred: ${e}`);
        } finally {
            setLoading(false);
        }
    };

    // Use 'post' method to handle "create" requests.
    const createEvent = async () => {
        try {
            const res = await fetch(
                `${API_BASE_URL}/createEvent`,
                // `http://localhost:8080/api_war_exploded/createEvent`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ name: eventName }), // eventName sent as a request
                }
            );
        } catch (e) {
            // setData(`woops! an error occurred: ${e}`);
        }
    };

    return (
        <>
            <h1>SWEN90007 Project Part b</h1>
            <p>Simple page for creating a new event or searching an existing event.</p>
            <div id='text-input'>
                <label>Please input the event name: </label>
                <input
                    placeholder='Event Name'
                    value={eventName}
                    onChange={(e) => setEventName(e.target.value)}
                ></input>
                {data && <p>{data}</p>}
                {loading && <p>loading...</p>}
            </div>
            <div id='button'>
                <button
                    disabled={loading}
                    type="button"
                    onClick={getEvent}
                >
                    Search Event
                </button>
            </div>
            <div id='button'>
                <button
                    // disabled={loading}
                    type="button"
                    onClick={createEvent}
                >
                    Create Event
                </button>
            </div>
        </>
    );
}

export default App;