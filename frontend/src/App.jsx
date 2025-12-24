import { useState } from 'react'
import axios from 'axios'
import './App.css'

function App() {
    const [token, setToken] = useState('')
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')

    // 1. Handle Registration
    const handleRegister = async () => {
        try {
            await axios.post('http://localhost:8080/api/auth/register', { username, password })
            setError('Registration successful! Please login.')
        } catch (err) {
            setError('Registration failed. Username might be taken.')
        }
    }

    // 2. Handle Login
    const handleLogin = async () => {
        try {
            const response = await axios.post('/api/auth/login', { username, password })
            setToken(response.data.token) // Save the token!
            setError('')
        } catch (err) {
            setError('Login failed. Check credentials.')
        }
    }

    // 3. The Dashboard (Only visible if logged in)
    if (token) {
        return (
            <div className="app-container">
                <h1>🔓 Vault Unlocked</h1>
                <p>Welcome, {username}!</p>
                <div className="card">
                    <p>Your Secret Token is safe.</p>
                </div>
                <button onClick={() => setToken('')}>Logout</button>
            </div>
        )
    }

    // 4. The Login Screen (Visible if NOT logged in)
    return (
        <div className="app-container">
            <h1>🔐 The Vault</h1>
            <p>Secure Password Manager</p>

            <div className="card login-form">
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <div className="button-group">
                    <button onClick={handleLogin}>Login</button>
                    <button onClick={handleRegister}>Register</button>
                </div>
            </div>

            {error && <p style={{color: 'red'}}>{error}</p>}
        </div>
    )
}

export default App