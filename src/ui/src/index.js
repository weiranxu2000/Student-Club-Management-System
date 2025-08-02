import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
//import {BrowserRouter as Router, Routes, Route,Link} from 'react-router-dom';
import {
    BrowserRouter as Router,
    Routes,
    Route,

} from 'react-router-dom';

//import AppTest from './App.test';
import Login from './Login';
import TransitionPage from "./TransitionPage";
import StudentDashboard from "./StudentDashboard";
import ClubManagementPage from "./ClubManagementPage";
import FacultyDashboard from "./FacultyDashboard";
//import App from './App';


const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    //<BrowserRouter>
        <React.StrictMode>
            <Router>
                <Routes>
                    <Route path="/" element={<Login />} />
                    <Route path="/transition" element={<TransitionPage  />} />
                    <Route path="/student-dashboard" element={<StudentDashboard />} />
                    <Route path="/club-management" element={<ClubManagementPage />} />
                    <Route path="/faculty-dashboard" element={<FacultyDashboard />} />
                </Routes>
            </Router>
        </React.StrictMode>
    //</BrowserRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
