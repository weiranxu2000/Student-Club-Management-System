import React, { useState } from 'react';
import './Login.css';
import { useNavigate } from 'react-router-dom';

// This is the basic
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
// const API_BASE_URL = "http://localhost:8080/api_war_exploded";

function Login() {
    const [id, setId] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('Student');
    const navigate = useNavigate();

    const handleLogin = async (event) => {
        event.preventDefault();

        const idInt = parseInt(id, 10);
        if (isNaN(idInt)) {
            console.error('ID must be integers');
            return;
        }

        console.log('Logging in with', idInt, password, role);

        if (role === 'Student') {//
            try {
                const response = await fetch(`${API_BASE_URL}/login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ id: idInt, password: password, role: role })
                    //credentials: 'include'
                });

                const data = await response.json();
                if (data.success) {
                    const is_admin = data.is_admin;

                    navigate('/transition', { state: {studentId: idInt, is_admin: is_admin} });

                } else {
                    alert('Login failed');
                }
            } catch (error) {
                console.error('Login error:', error);
            }

        } else if (role === 'Faculty Administrator') {
            try {
                const response = await fetch(`${API_BASE_URL}/loginAdmin`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ id: idInt, password: password, role: role })
                });

                const data = await response.json();
                if (data.success) {
                    navigate('/faculty-dashboard', { state: { adminId: idInt } });
                } else {
                    alert('Login failed');
                }
            } catch (error) {
                console.error('Login error:', error);
            }
        }
    };

    return (


        <div className="login-container">
            <img src="/UoM_Logo.png" alt="School Logo" className="school-logo" />

            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <div className="form-group">
                    <label htmlFor="id">ID:</label>
                    <input
                        type="number"
                        id="id"
                        value={id}
                        onChange={(e) => setId(e.target.value)}
                        placeholder="Enter your ID"
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter your password"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="role">Role:</label>
                    <select
                        id="role"
                        value={role}
                        onChange={(e) => setRole(e.target.value)} // 设置角色
                        required
                    >
                        <option value="Student">Student</option>
                        <option value="Faculty Administrator">Faculty Administrator</option>
                    </select>
                </div>

                <button type="submit" className="login-btn">Login</button>

            </form>
        </div>
    );
}



export default Login;
