import React, { useState, useEffect } from 'react';
import './ClubManagementPage.css'; // Introducing styles
import { useNavigate, useLocation } from 'react-router-dom'; // 用于返回其他页面
// const API_BASE_URL = "http://localhost:8080/api_war_exploded";
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;


const ClubManagementPage = () => {
    //const [events, setEvents] = useState(clubEventsData);
    const navigate = useNavigate();
    const location = useLocation();
    const { studentId, is_admin } = location.state;  // Get the ID of the currently logged in student
    const [events, setEvents] = useState([]); //Use this line after adding the backend
    const [searchQuery, setSearchQuery] = useState('');
    //const [editEvent, setEditEvent] = useState(null); // Events currently being edited
    const [editEventId, setEditEventId] = useState(null);  // Activity ID currently being edited
    const [editEventData, setEditEventData] = useState({});  // Edit the data at the time of the event
    const [addEvent, setAddEvent] = useState(false); // Added activity status
    const [newAdminId, setNewAdminId] = useState('');
    const [newAdminClub, setNewAdminClub] = useState('');
    const [manageAdmins, setManageAdmins] = useState(false); // Managing Administrator Status
    const [applyFunding, setApplyFunding] = useState(false); // Status of funds requested

    const [fundingList, setFundingList] = useState([]);
    const [showFundingList, setShowFundingList] = useState(false);
    const [editFundingId, setEditFundingId] = useState(null);
    const [editFundingData, setEditFundingData] = useState({});

    const [admins, setAdmins] = useState([]); // Admin data
    const [newEventData, setNewEventData] = useState({
        title: '',
        club_name: '',
        description: '',
        venue_name: '',
        date: '',
        time: '',
        cost: null,
        admin_id: studentId,
        // status: 'open',
        //capacity: '',
    });
    // Request for funding form data
    const [fundingData, setFundingData] = useState({
        description: '',
        amount: '',
        club_name: '',
        admin_id: studentId
    });


    // Get the filtered activity data, only the activities of the clubs that are currently logged in the student management are shown here.
    useEffect(() => {
        const fetchEvents = async () => {
            try {
                const response = await fetch(`${API_BASE_URL}/getAllEventAdmin?id=${studentId}`,{
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                    //credentials: 'include'

                });  // call backend API

                // check response
                // if (response.status === 401) {
                //     // if 401 Unauthorized，transfer to loginpage automately
                //     navigate('/');
                //     return;
                // }
                // if (response.status === 403) {
                //     // if 403 Unauthorized，alert
                //     alert("You are not a student admin.");
                //     return;
                // }

                const data = await response.json();
                setEvents(data);
            } catch (error) {
                console.error('Error fetching events:', error);
            }
        };

        fetchEvents();
    }, [studentId]);


    useEffect(() => {
        const fetchAdmins = async () => {
            try {
                const response = await fetch(`${API_BASE_URL}/getAllClubAdmin?id=${studentId}`,{
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
                // if (response.status === 403) {
                //     alert("You are not a student admin.");
                //     return;
                // }

                const data = await response.json();
                setAdmins(data);
            } catch (error) {
                console.error('Error fetching admins:', error);
            }
        };

        fetchAdmins();
    }, []);

    useEffect(() => {
        const fetchFundingList = async () => {
            try {
                const response = await fetch(`${API_BASE_URL}/getAllApplicationAdmin?id=${studentId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    credentials: 'include'
                });


                    const data = await response.json();
                    setFundingList(data);

            } catch (error) {
                console.error('Error fetching funding list:', error);
            }
        };
        fetchFundingList();
    }, []);

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
                // if (response.status === 403) {
                //     alert("You are not a student admin.");
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

    const handleEdit = (eventId) => {
        const eventToEdit = events.find(event => event.id === eventId);
        setEditEventId(eventId);
        setEditEventData(eventToEdit);
    };

    const handleEditInputChange = (e) => {
        const { name, value } = e.target;
        setEditEventData({ ...editEventData, [name]: value });
    };

    const handleSubmitEditEvent = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/modifyEvent`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(editEventData)
                //credentials: 'include'
            });

            // if (response.status === 401) {
            //     navigate('/');
            //     return;
            // }
            // if (response.status === 403) {
            //     alert("You are not a student admin.");
            //     return;
            // }

            if (response.ok) {
                alert('Event updated successfully!');
                try {
                    const response = await fetch(`${API_BASE_URL}/getAllEventAdmin?id=${studentId}`,{
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        credentials: 'include'

                    });

                    if (response.status === 401) {
                        navigate('/');
                        return;
                    }
                    if (response.status === 403) {
                        alert("You are not a student admin.");
                        return;
                    }


                    const data = await response.json();
                    setEvents(data);
                } catch (error) {
                    console.error('Error fetching events:', error);
                }
                handleCloseEdit();
            } else {
                alert('Failed to edit event');
            }
        } catch (error) {
            console.error('Error editing event:', error);
        }
    };

    const handleDeleteEvent = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/cancelEvent`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(editEventData),
                credentials: 'include'
            });

            if (response.status === 401) {
                navigate('/');
                return;
            }
            if (response.status === 403) {
                alert("You are not a student admin.");
                return;
            }

            if (response.ok) {
                alert('Event deleted successfully!');
                try {
                    const response = await fetch(`${API_BASE_URL}/getAllEventAdmin?id=${studentId}`,{
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        credentials: 'include'

                    });

                    if (response.status === 401) {
                        navigate('/');
                        return;
                    }
                    if (response.status === 403) {
                        alert("You are not a student admin.");
                        return;
                    }

                    const data = await response.json();
                    setEvents(data);
                } catch (error) {
                    console.error('Error fetching events:', error);
                }
                handleCloseEdit();
            } else {
                alert('Failed to delete event');
            }
        } catch (error) {
            console.error('Error deleting event:', error);
        }
    };

    const handleCloseEdit = () => {
        setEditEventId(null);
        setEditEventData({});
    };

    const handleAddEvent = () => {
        setAddEvent(true);
    };

    const handleCloseAddEvent = () => {
        setAddEvent(false);
        setNewEventData({
            title: '',
            club_name: '',
            description: '',
            venue_name: '',
            date: '',
            time: '',
            cost: null,
            admin_id: studentId,
            // status: '',
            // capacity: '',

        });
    };
    const handleSubmitNewEvent = async () => {

        try {
            const response = await fetch(`${API_BASE_URL}/createEvent`,  {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newEventData),
                credentials: 'include'
            });

            if (response.status === 401) {
                navigate('/');
                return;
            }
            if (response.status === 403) {
                alert("You are not a student admin of the club.");
                return;
            }

            if (response.ok) {
                alert('Event created successfully!');
                try {
                    const response = await fetch(`${API_BASE_URL}/getAllEventAdmin?id=${studentId}`,{
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        credentials: 'include'

                    });

                    if (response.status === 401) {
                        navigate('/');
                        return;
                    }
                    if (response.status === 403) {
                        alert("You are not a student admin.");
                        return;
                    }

                    const data = await response.json();
                    setEvents(data);
                } catch (error) {
                    console.error('Error fetching events:', error);
                }
                handleCloseAddEvent();
            } else {
                alert('Failed to create event');
            }
        } catch (error) {
            alert('Error submitting event:', error);
        }
    };

    const handleManageAdmins = () => {
        setManageAdmins(true);
    };

    const handleCloseManageAdmins = () => {
        setManageAdmins(false);
        setNewAdminId('');
        setNewAdminClub('');
    };

    const handleAddAdmin = async () => {
        if (newAdminId && newAdminClub) {
            const newAdminObj = {
                student_id: newAdminId,
                club_name: newAdminClub,
                admin_id: studentId,
            };

            try {
                const response = await fetch(`${API_BASE_URL}/addAdmin`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(newAdminObj),
                    credentials: 'include'
                });

                if (response.status === 401) {
                    navigate('/');
                    return;
                }
                if (response.status === 403) {
                    alert("You are not a student admin.");
                    return;
                }


                if (response.ok) {
                    alert('Admin added successfully!');
                    setNewAdminId('');
                    setNewAdminClub('');
                    try {
                        const response = await fetch(`${API_BASE_URL}/getAllClubAdmin?id=${studentId}`,{
                            method: 'GET',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            credentials: 'include'

                        });

                        if (response.status === 401) {
                            navigate('/');
                            return;
                        }
                        if (response.status === 403) {
                            alert("You are not a student admin.");
                            return;
                        }

                        const data = await response.json();
                        setAdmins(data);
                    } catch (error) {
                        console.error('Error fetching admins:', error);
                    }
                } else {
                    alert('Failed to add admin');
                }
            } catch (error) {
                console.error('Error adding admin:', error);
            }
        }
    };


    const handleRemoveAdmin = async (adminId, adminClub) => {
        const deleteAdminObj = {
            student_id: adminId,
            club_name: adminClub
        };
        try {
            const response = await fetch(`${API_BASE_URL}/deleteAdmin`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(deleteAdminObj),
                credentials: 'include'
            });

            if (response.status === 401) {
                navigate('/');
                return;
            }
            if (response.status === 403) {
                alert("You are not a student admin.");
                return;
            }

            if (response.ok) {
                alert('Admin removed successfully!');
                try {
                    const response = await fetch(`${API_BASE_URL}/getAllClubAdmin?id=${studentId}`,{
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        credentials: 'include'

                    });

                    if (response.status === 401) {
                        navigate('/');
                        return;
                    }
                    if (response.status === 403) {
                        alert("You are not a student admin.");
                        return;
                    }

                    const data = await response.json();
                    setAdmins(data);
                } catch (error) {
                    console.error('Error fetching admins:', error);
                }
            } else {
                alert('Failed to remove admin');
            }
        } catch (error) {
            console.error('Error removing admin:', error);
        }
    };

    const handleApplyFunding = () => {
        setApplyFunding(true);
    };

    const handleCloseApplyFunding = () => {
        setApplyFunding(false);
        setFundingData({
            description: '',
            amount: '',
            club_name: '',
            admin_id: studentId
        });
    };

    const handleSubmitFunding = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/createApplication`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(fundingData),
                credentials: 'include'
            });

            if (response.status === 401) {
                navigate('/');
                return;
            }
            if (response.status === 403) {
                alert("You are not a student admin.");
                return;
            }

            if (response.ok) {
                alert('Funding request submitted successfully!');
                handleCloseApplyFunding();
            } else {
                alert('Failed to submit funding request');
            }
        } catch (error) {
            console.error('Error submitting funding request:', error);
        }
    };

    const handleModifyFunding = () => {
        setShowFundingList(true);
    }

    const handleCloseFundingList = () => {
        setShowFundingList(false);
    };

    const handleEditFunding = (fundingId) => {
        const fundingToEdit = fundingList.find(funding => funding.id === fundingId);
        setEditFundingId(fundingId);
        setEditFundingData(fundingToEdit);
    };

    const handleEditFundingInputChange = (e) => {
        const { name, value } = e.target;
        setEditFundingData({ ...editFundingData, [name]: value });
    };

    const handleSubmitEditFunding = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/modifyApplication`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(editFundingData),
                credentials: 'include'
            });

            if (response.ok) {
                alert('Funding request updated successfully!');
                try {
                    const response = await fetch(`${API_BASE_URL}/getAllApplicationAdmin?id=${studentId}`, {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        credentials: 'include'
                    });

                    if (response.status === 401) {
                        navigate('/');
                        return;
                    }
                    if (response.status === 403) {
                        alert("You are not a student admin.");
                        return;
                    }

                    const data = await response.json();
                    setFundingList(data);
                } catch (error) {
                    console.error('Error fetching funding list:', error);
                }
                handleCloseEditFunding();
            } else {
                alert('Failed to update funding request');
            }
        } catch (error) {
            alert('Error updating funding request:', error);
        }
    };

    const handleDeleteFunding = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/cancelApplication`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(editFundingData),
                credentials: 'include'
            });

            if (response.status === 401) {
                navigate('/');
                return;
            }
            if (response.status === 403) {
                alert("You are not a student admin.");
                return;
            }

            if (response.ok) {
                alert('Funding request deleted successfully!');
                try {
                    const response = await fetch(`${API_BASE_URL}/getAllApplicationAdmin?id=${studentId}`, {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        credentials: 'include'
                    });

                    if (response.status === 401) {
                        navigate('/');
                        return;
                    }
                    if (response.status === 403) {
                        alert("You are not a student admin.");
                        return;
                    }

                    const data = await response.json();
                    setFundingList(data);
                } catch (error) {
                    console.error('Error fetching funding list:', error);
                }
                handleCloseEditFunding();
            } else {
                alert('Failed to delete funding request');
            }
        } catch (error) {
            console.error('Error deleting funding request:', error);
        }
    };

    const handleCloseEditFunding = () => {
        setEditFundingId(null);
        setEditFundingData({});
    };

    const filteredEvents = events.filter((event) =>
        event.club_name.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return (
        <div className="management-wrapper">
            <button
                className="back-btn"
                onClick={() => navigate(`/transition`, {state: {studentId, is_admin}})}
            >
                Back
            </button>

            <div className="management-container">
                <h2>Manage Club </h2>

                <div className="search-container">
                    <input
                        type="text"
                        placeholder="Search by club name..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="search-box"
                    />

                    <button className="add-event-btn" onClick={handleAddEvent}>
                        Add New Event
                    </button>

                    <button className="manage-admins-btn" onClick={handleManageAdmins}>
                        Manage Admins
                    </button>

                    <button className="apply-funding-btn" onClick={handleApplyFunding}>
                        Apply for Funding
                    </button>

                    <button className="modify-funding-btn" onClick={handleModifyFunding}>
                        View/Modify Funding
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
                            <th>Edit</th>
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
                                        <button
                                            className="edit-btn"
                                            onClick={() => handleEdit(event.id)}
                                        >
                                            Edit
                                        </button>
                                    </td>
                                </tr>

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

                {editEventId && (
                    <div className="edit-event-overlay">
                        <div className="edit-event">
                            <h3>Edit Event</h3>
                            <input
                                type="text"
                                name="title"
                                value={editEventData.title}
                                onChange={handleEditInputChange}
                                placeholder="Title"
                            />
                            <input
                                type="text"
                                name="club_name"
                                value={editEventData.club_name}
                                onChange={handleEditInputChange}
                                placeholder="Club Name"
                            />
                            <input
                                type="text"
                                name="description"
                                value={editEventData.description}
                                onChange={handleEditInputChange}
                                placeholder="Description"
                            />
                            <input
                                type="text"
                                name="venue_name"
                                value={editEventData.venue_name}
                                onChange={handleEditInputChange}
                                placeholder="Venue Name"
                            />
                            <input
                                type="date"
                                name="date"
                                value={editEventData.date}
                                onChange={handleEditInputChange}
                            />
                            <input
                                type="time"
                                name="time"
                                value={editEventData.time}
                                onChange={handleEditInputChange}
                            />
                            <input
                                type="number"
                                name="cost"
                                value={editEventData.cost}
                                onChange={handleEditInputChange}
                                placeholder="Cost"
                            />
                            {/*<input*/}
                            {/*    type="number"*/}
                            {/*    name="capacity"*/}
                            {/*    value={editEventData.capacity}*/}
                            {/*    onChange={handleEditInputChange}*/}
                            {/*    placeholder="Capacity"*/}
                            {/*/>*/}
                            <button onClick={handleSubmitEditEvent} className="save-btn">Save</button>
                            <button onClick={handleCloseEdit} className="close-btn">Close</button>
                            <button onClick={handleDeleteEvent} className="delete-btn">Delete Event</button>
                        </div>
                    </div>
                )}

                {addEvent && (
                    <div className="add-event-overlay">
                        <div className="add-event">
                            <h3>Add New Event</h3>
                            <input
                                type="text"
                                placeholder="Title"
                                value={newEventData.title}
                                onChange={(e) => setNewEventData({...newEventData, title: e.target.value})}
                            />
                            <input
                                type="text"
                                placeholder="Club Name"
                                value={newEventData.club_name}
                                onChange={(e) => setNewEventData({...newEventData, club_name: e.target.value})}
                            />
                            <input
                                type="text"
                                placeholder="Description"
                                value={newEventData.description}
                                onChange={(e) => setNewEventData({...newEventData, description: e.target.value})}
                            />
                            <input
                                type="text"
                                placeholder="Venue Name"
                                value={newEventData.venue_name}
                                onChange={(e) => setNewEventData({...newEventData, venue_name: e.target.value})}
                            />
                            <input
                                type="date"
                                value={newEventData.date}
                                onChange={(e) => setNewEventData({...newEventData, date: e.target.value})}
                            />
                            <input
                                type="time"
                                value={newEventData.time}
                                onChange={(e) => setNewEventData({...newEventData, time: e.target.value})}
                            />
                            <input
                                type="number"
                                placeholder="Cost"
                                value={newEventData.cost}
                                onChange={(e) => setNewEventData({...newEventData, cost: e.target.value})}
                            />
                            {/*<input*/}
                            {/*    type="number"*/}
                            {/*    placeholder="Capacity"*/}
                            {/*    value={newEventData.capacity}*/}
                            {/*    onChange={(e) => setNewEventData({ ...newEventData, capacity: e.target.value })}*/}
                            {/*/>*/}
                            <button onClick={handleSubmitNewEvent} className="save-btn">Save</button>
                            <button onClick={handleCloseAddEvent} className="close-btn">Close</button>
                        </div>
                    </div>
                )}

                {manageAdmins && (
                    <div className="manage-admins-overlay">
                        <div className="manage-admins">
                            <h3>Manage Admins</h3>
                            <table>
                                <thead>
                                <tr>
                                    <th>Admin ID</th>
                                    <th>Admin Name</th>
                                    <th>Club</th>
                                    <th>Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                {admins.map((admin) => (
                                    <tr key={admin.id}>
                                        <td>{admin.id}</td>
                                        <td>{admin.name}</td>
                                        <td>{admin.club}</td>
                                        <td>
                                            <button onClick={() => handleRemoveAdmin(admin.id, admin.club)}>Remove</button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>

                            <input
                                type="text"
                                placeholder="Enter new admin ID"
                                value={newAdminId}
                                onChange={(e) => setNewAdminId(e.target.value)}
                            />

                            <input
                                type="text"
                                placeholder="Enter club name"
                                value={newAdminClub}
                                onChange={(e) => setNewAdminClub(e.target.value)}
                            />

                            <button
                                onClick={handleAddAdmin}
                                disabled={!newAdminId || !newAdminClub}
                            >
                                Add Admin
                            </button>

                            <button onClick={handleCloseManageAdmins} className="close-btn">Close</button>
                        </div>
                    </div>
                )}

                {applyFunding && (
                    <div className="apply-funding-overlay">
                        <div className="apply-funding">
                            <h3>Apply for Funding</h3>
                            <input
                                type="text"
                                placeholder="Description"
                                value={fundingData.description}
                                onChange={(e) => setFundingData({...fundingData, description: e.target.value})}
                            />
                            <input
                                type="number"
                                placeholder="Amount"
                                value={fundingData.amount}
                                onChange={(e) => setFundingData({...fundingData, amount: e.target.value})}
                            />
                            <input
                                type="text"
                                placeholder="Club Name"
                                value={fundingData.club_name}
                                onChange={(e) => setFundingData({...fundingData, club_name: e.target.value})}
                            />
                            <button onClick={handleSubmitFunding} className="save-btn">Submit</button>
                            <button onClick={handleCloseApplyFunding} className="close-btn">Close</button>
                        </div>
                    </div>
                )}

                {showFundingList && (
                    <div className="funding-list-overlay">
                        <div className="funding-list">
                            <h3>Funding Requests</h3>
                            <table>
                                <thead>
                                <tr>
                                    <th>Club Name</th>
                                    <th>Description</th>
                                    <th>Amount</th>
                                    <th>Time</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                {fundingList.map((funding) => (
                                    <tr key={funding.id}>
                                        <td>{funding.clubName}</td>
                                        <td>{funding.description}</td>
                                        <td>${funding.amount}</td>
                                        <td>{funding.date}</td>
                                        <td>{funding.status}</td>
                                        <td>
                                            <button onClick={() => handleEditFunding(funding.id)}>Edit</button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                            <button onClick={handleCloseFundingList} className="close-btn">Close</button>
                        </div>
                    </div>
                )}

                {editFundingId && (
                    <div className="edit-funding-overlay">
                        <div className="edit-funding">
                            <h3>Edit Funding Request</h3>
                            <p><strong>Club Name:</strong> {editFundingData.clubName}</p>
                            <input
                                type="text"
                                name="description"
                                value={editFundingData.description}
                                onChange={handleEditFundingInputChange}
                                placeholder="Description"
                            />
                            <input
                                type="number"
                                name="amount"
                                value={editFundingData.amount}
                                onChange={handleEditFundingInputChange}
                                placeholder="Amount"
                            />
                            {/*<input*/}
                            {/*    type="time"*/}
                            {/*    name="time"*/}
                            {/*    value={editFundingData.time}*/}
                            {/*    onChange={handleEditFundingInputChange}*/}
                            {/*/>*/}
                            <button onClick={handleSubmitEditFunding} className="save-btn">Save</button>
                            <button onClick={handleCloseEditFunding} className="close-btn">Cancel</button>
                            <button onClick={handleDeleteFunding} className="delete-btn">Delete Request</button>
                        </div>
                    </div>
                )}

            </div>
        </div>
    );
};

export default ClubManagementPage;
