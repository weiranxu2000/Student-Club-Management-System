import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './FacultyDashboard.css';

// const API_BASE_URL = "http://localhost:8080/api_war_exploded";
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

function FacultyDashboard() {
    const [fundingApplications, setFundingApplications] = useState([]);
    const [expandedApplication, setExpandedApplication] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();
    const { adminId } = location.state;

    useEffect(() => {
        fetchFundingApplications();
    }, []);

    const fetchFundingApplications = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/getAllApplication`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include'
            });


                const data = await response.json();
                setFundingApplications(data);

        } catch (error) {
            console.error('Error fetching funding applications:', error);
        }
    };

    const handleReview = async (applicationId) => {
        try {
            const response = await fetch(`${API_BASE_URL}/reviewApplication`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ id: applicationId }),
                credentials: 'include'
            });

            if (response.ok) {
                setExpandedApplication(applicationId);
                setFundingApplications(fundingApplications.map(app =>
                    app.id === applicationId ? {...app, status: 'inReview'} : app
                ));
            } else {
                console.error('Failed to update application status');
            }
        } catch (error) {
            console.error('Error updating application status:', error);
        }
    };

    const handleDecision = async (applicationId, decision) => {
        try {
            let endpoint;
            if (decision === 'Approved') {
                endpoint = `${API_BASE_URL}/approveApplication`;
            } else if (decision === 'Rejected') {
                endpoint = `${API_BASE_URL}/rejectApplication`;
            } else {
                console.error('Invalid decision');
                return;
            }

            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ id: applicationId }),
                credentials: 'include'
            });

            if (response.ok) {
                setFundingApplications(fundingApplications.map(app =>
                    app.id === applicationId ? {...app, status: decision} : app
                ));
                setExpandedApplication(null);
            } else {
                console.error(`Failed to ${decision.toLowerCase()} application`);
            }
        } catch (error) {
            console.error(`Error ${decision.toLowerCase()}ing application:`, error);
        }
    };

    return (
        <div className="dashboard-wrapper">
            <div className="dashboard-container">
            <h1>Faculty Administrator Dashboard</h1>
            <div className="scroll-container">
            <table className="funding-applications">
                <thead>
                <tr>
                    <th>Club Name</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {fundingApplications.map(application => (
                    <React.Fragment key={application.id}>
                        <tr>
                            <td>{application.club_name}</td>
                            <td>{application.status}</td>
                            <td>
                                {// application.status !== 'inReview' &&
                                    application.status !== 'approved' &&
                                    application.status !== 'rejected' && (
                                        <button onClick={() => handleReview(application.id)}>Review</button>
                                    )}
                            </td>
                        </tr>
                        {expandedApplication === application.id && (
                            <tr>
                                <td colSpan="3">
                                    <div className="expanded-details">
                                        <p><strong>Description:</strong> {application.description}</p>
                                        <p><strong>Amount:</strong> ${application.amount}</p>
                                        <p><strong>Date:</strong> {application.date}</p>
                                        <button onClick={() => handleDecision(application.id, 'Approved')}>Approve</button>
                                        <button onClick={() => handleDecision(application.id, 'Rejected')}>Reject</button>
                                    </div>
                                </td>
                            </tr>
                        )}
                    </React.Fragment>
                ))}
                </tbody>
            </table>
            </div>
        </div>
        </div>
    );
}

export default FacultyDashboard;
