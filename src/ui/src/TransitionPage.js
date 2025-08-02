import React from 'react';
import './TransitionPage.css';
import { useNavigate,useLocation } from 'react-router-dom';
// const API_BASE_URL = "http://localhost:8080/api_war_exploded";
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

function TransitionPage() {
    const navigate = useNavigate();
    const location = useLocation();

    const { studentId, is_admin } = location.state;

    const handleStudentDashboard = () => {
        navigate('/student-dashboard', { state: { studentId, is_admin } });
    };

    const handleClubManagement =async () => {
        if (is_admin) {
            navigate('/club-management', { state: { studentId, is_admin  } });
        } else {
            alert("You are not a club administrator.");
        }
        // try {
        //     const response = await fetch(`${API_BASE_URL}/getAllEventAdmin?id=${studentId}`,{
        //         method: 'GET',
        //         headers: {
        //             'Content-Type': 'application/json',
        //         }
        //         credentials: 'include'
        //     });
        //     if (response.status === 401) {
        //         navigate('/');
        //         return;
        //     }
        //     if (response.status === 403) {
        //         alert("You are not a student admin.");
        //         return;
        //
        //     }
        //
        //     const data = await response.json(); // 解析JSON
        //     navigate('/club-management', { state: { studentId, is_admin  } });
        // } catch (error) {
        //     console.error('Error fetching events:', error);
        // }
    };

    return (
        <div className="transition-page">
            <div className="circle-container">
                <div className="circle" onClick={handleStudentDashboard}>
                    <img src="/icon_search.png" alt="Search Icon" />
                    <p>View & Apply Activities </p>
                </div>

                <div className="circle" onClick={handleClubManagement}>
                    <img src="/icon_club.png" alt="Club Icon" />
                    <p>Club Management</p>
                </div>
            </div>
        </div>
    );
}

export default TransitionPage;
