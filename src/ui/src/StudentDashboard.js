import React, { useState, useEffect } from 'react';
import './StudentDashboard.css';
import { useNavigate,useLocation } from 'react-router-dom';

// const API_BASE_URL = "http://localhost:8080/api_war_exploded";
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

const StudentDashboard = () => {
    //const [events, setEvents] = useState(eventsData);
    const navigate = useNavigate();
    const location = useLocation();
    const { studentId, is_admin } = location.state;
    const [events, setEvents] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [viewRsvp, setViewRsvp] = useState(false);
    const [tickets, setTickets] = useState([]);
    const [rsvpFormVisible, setRsvpFormVisible] = useState(false);
    const [attendees, setAttendees] = useState([null]);
    const [ticketCount, setTicketCount] = useState(1);
    const [currentEvent, setCurrentEvent] = useState(null); //

    const handleExpand = async (eventId) => {
        const event = events.find((event) => event.id === eventId);
        if (!event.detailedFetched) {
            try {
                const response = await fetch(`${API_BASE_URL}/moreInfo?id=${eventId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                    //credentials: 'include'
                });

                // if (response.status === 401) {
                //     navigate('/');
                //     return;
                // }

                const data = await response.json();

                setEvents((prevEvents) =>
                    prevEvents.map((event) =>
                        event.id === eventId ? { ...event, ...data, expanded: true, detailedFetched: true } : event
                    )
                );
            } catch (error) {
                console.error('Error fetching event details:', error);
            }
        } else {
            setEvents((prevEvents) =>
                prevEvents.map((event) =>
                    event.id === eventId ? { ...event, expanded: !event.expanded } : event
                )
            );
        }
    };

    const filteredEvents = events.filter((event) =>
        event.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        event.club_name.toLowerCase().includes(searchQuery.toLowerCase())
    );

    useEffect(() => {
        const fetchEvents = async () => {
            try {
                const response = await fetch(`${API_BASE_URL}/getAllEvent`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                    //credentials: 'include'

                });

                // if (response.status === 401) {
                //     navigate('/');
                //     return;
                // }

                const data = await response.json();
                setEvents(data);
            } catch (error) {
                console.error('Error fetching events:', error);
            }
        };

        fetchEvents();
    }, []);

    useEffect(() => {
        const fetchTickets = async () => {
            try {
                const response = await fetch(`${API_BASE_URL}/getAllTicket?student_id=${studentId}`,{
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                    //credentials: 'include'

                });

                const data = await response.json();
                setTickets(data);

            } catch (error) {
                console.error('Error fetching RSVPs:', error);
            }
        };
        fetchTickets();
    }, [studentId]);

    const handleViewRsvp = () => {
        setViewRsvp(true);
    };

    const handleCloseRsvpView = () => {
        setViewRsvp(false);
    };

    const handleCancelTicket = async (ticket) => {
        try {
            const response = await fetch(`${API_BASE_URL}/cancelTicket`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    rsvp_id: ticket.rsvp_id,
                    student_id: ticket.student_id,
                })
                //credentials: 'include'
            });

            if (response.ok) {

                try {
                    const response = await fetch(`${API_BASE_URL}/getAllTicket?student_id=${studentId}`,{
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        }
                        //credentials: 'include'

                    });

                    const data = await response.json();
                    setTickets(data);

                } catch (error) {
                    console.error('Error fetching RSVPs:', error);
                }
                try {
                    const response = await fetch(`${API_BASE_URL}/getAllEvent`, {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        //credentials: 'include'

                    });

                    const data = await response.json();
                    setEvents(data);
                } catch (error) {
                    console.error('Error fetching events:', error);
                }
                alert("RSVP canceled successfully!");
            } else {
                alert('Failed to cancel RSVP.');
            }
        } catch (error) {
            console.error('Error cancelling RSVP:', error);
        }
    };

    const handleRsvpClick = (event) => {
        setRsvpFormVisible(true);
        setCurrentEvent(event);
    };

    const handleCloseRsvpForm = () => {
        setRsvpFormVisible(false);
        setAttendees([null]);
        setTicketCount(1);
    };

    const handleAttendeeChange = (index, value) => {
        const updatedAttendees = [...attendees];
        updatedAttendees[index] = value;
        setAttendees(updatedAttendees);
    };

    const handleAddAttendee = () => {
        setAttendees([...attendees, 0]);
    };

    const handleRemoveAttendee = (index) => {
        const updatedAttendees = attendees.filter((_, i) => i !== index);
        setAttendees(updatedAttendees);
    };

    const handleSubmitRsvp = async (event) => {
        if (attendees.length !== ticketCount) {
            alert("The number of tickets must match the number of attendees.");
            return;
        }

        console.log(JSON.stringify({
            student_id: studentId,
            event_id: currentEvent.id,
            attendees: attendees,
            ticket_count: ticketCount,
        }));

        try {
            const response = await fetch(`${API_BASE_URL}/createRSVP`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    student_id: studentId,
                    event_id: currentEvent.id,
                    attendees: attendees,
                    ticket_count: ticketCount,
                })
                //credentials: 'include'
            });

            // if (response.status === 401) {
            //     navigate('/');
            //     return;
            // }

            const result = await response.json();

            if (response.ok) {
                alert("RSVP submitted successfully!");
                try {
                    const response = await fetch(`${API_BASE_URL}/getAllEvent`, {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        }
                        //credentials: 'include'

                    });
                    const data = await response.json();
                    setEvents(data);
                } catch (error) {
                    console.error('Error fetching events:', error);
                }
                try {
                    const response = await fetch(`${API_BASE_URL}/getAllTicket?student_id=${studentId}`, {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        }
                        //credentials: 'include'
                    });

                    // if (response.status === 401) {
                    //     navigate('/');
                    //     return;
                    // }

                    const data = await response.json();
                    setTickets(data);

                } catch (error) {
                    console.error('Error fetching RSVPs:', error);
                }
                setRsvpFormVisible(false);
            } else {
                alert(`RSVP failed: ${result.message}`);
            }
        } catch (error) {
            console.error('Error submitting RSVP:', error);
        }
    };
    //////////////////////////////////////////////////////////////////////////////////

    return (
        <div className="dashboard-wrapper">
            <button
                className="back-btn"
                onClick={() => navigate(`/transition`, {state: {studentId, is_admin}})}
            >
                Back
            </button>

            <div className="dashboard-container">
                <h2>All Activities</h2>

                <div className="search-container">
                    <input
                        type="text"
                        placeholder="Search by title or club name"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="search-box"
                    />

                    <button className="view-rsvp-btn" onClick={handleViewRsvp}>
                        View My RSVPs
                    </button>
                </div>
                <div className="scroll-container">
                    <table className="events-table">
                        <thead>
                        <tr>
                            <th>Title</th>
                            <th>Club Name</th>
                            <th>Date</th>
                            <th>Cost</th>
                            <th>Capacity</th>
                            <th>More Information</th>
                            <th>RSVP</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredEvents.map((event) => (
                            <React.Fragment key={event.id}>
                                <tr>
                                    <td>{event.title}</td>
                                    <td>{event.club_name}</td>
                                    <td>{event.date}</td>
                                    <td>{event.cost === 0 ? 'Free' : `$${event.cost}`}</td>
                                    <td>{event.rsvp_count}/{event.capacity}</td>
                                    <td>
                                        <button
                                            className="see-more-btn"
                                            onClick={() => handleExpand(event.id)}
                                        >
                                            {event.expanded ? 'Hide' : 'See More'}
                                        </button>
                                    </td>
                                    <td>
                                        {event.capacity > event.rsvp_count ? (
                                            <button className="rsvp-btn"
                                                    onClick={() => handleRsvpClick(event)}>RSVP</button>
                                        ) : (
                                            <span>Full</span>
                                        )}
                                    </td>
                                </tr>
                                {/* 展开的更多信息 */}
                                {event.expanded && (
                                    <tr>
                                        <td colSpan="7">
                                            <div className="event-details">
                                                <p><strong>Description:</strong> {event.description}</p>
                                                <p><strong>Venue Place:</strong> {event.venueName}</p>
                                                <p><strong>Status:</strong> {event.status}</p>
                                                <p><strong>Time:</strong> {event.time}</p>
                                            </div>
                                        </td>
                                    </tr>
                                )}
                            </React.Fragment>
                        ))}
                        </tbody>
                    </table>
                </div>
                {viewRsvp && (
                    <div className="rsvp-view-overlay">
                        <div className="rsvp-view">
                            <h3>My RSVPs</h3>
                            {tickets.map((ticket) => {
                                const ticketEvent = events.find((event) => event.id === ticket.event_id);
                                if(ticketEvent){
                                    return (
                                        <div key={`${ticket.event_id}-${ticket.student_id}`}>
                                            <p>Ticket for Event: {ticketEvent ? ticketEvent.title : "Unknown Event"},
                                                Student
                                                ID: {ticket.student_id}</p>

                                            <button onClick={() => handleCancelTicket(ticket)}>Cancel Ticket</button>
                                        </div>
                                    );}
                            })}
                            <button onClick={handleCloseRsvpView} className="close-btn">Close</button>
                        </div>
                    </div>
                )}
                {rsvpFormVisible && (
                    <div className="rsvp-form-overlay">
                        <div className="rsvp-form">
                            <h3>RSVP for {currentEvent.title}</h3>
                            {attendees.map((attendee, index) => (
                                <div key={index}>
                                    <input
                                        type="text"
                                        placeholder="Enter student id"
                                        value={attendee}
                                        onChange={(e) => handleAttendeeChange(index, e.target.value)}
                                    />
                                    {index > 0 && (
                                        <button onClick={() => handleRemoveAttendee(index)}>Remove</button>
                                    )}
                                </div>
                            ))}
                            <button onClick={handleAddAttendee}>Add Another Attendee</button>
                            <div>
                                <label>Number of Tickets: </label>
                                <input
                                    type="number"
                                    value={ticketCount}
                                    min={1}
                                    max={currentEvent.capacity - currentEvent.rsvp_count}
                                    onChange={(e) => setTicketCount(Number(e.target.value))}
                                />
                            </div>
                            <button onClick={handleSubmitRsvp}>Submit RSVP</button>
                            <button onClick={handleCloseRsvpForm}>Cancel</button>
                        </div>
                    </div>
                )}


            </div>
        </div>
    );
};

export default StudentDashboard;
