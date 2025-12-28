import { useState, useEffect } from 'react'
import api from './api/axiosConfig'; // <--- CHANGE 1: Import your custom tool
import './App.css'

function App() {
    const [token, setToken] = useState(localStorage.getItem('token') || '') // <--- TIP: Load from storage on refresh
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')

    // Dashboard State
    const [secrets, setSecrets] = useState([])
    const [newUrl, setNewUrl] = useState('')
    const [newUsername, setNewUsername] = useState('')
    const [newPassword, setNewPassword] = useState('')

    const [revealedId, setRevealedId] = useState(null)

    // --- AUTHENTICATION ---
    const handleRegister = async () => {
        try {
            // CHANGE 2: Use 'api' and remove 'http://localhost:8080'
            await api.post('/api/auth/register', { username, password })
            setError('Registration successful! Please login.')
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed.')
        }
    }

    const handleLogin = async () => {
        try {
            const response = await api.post('/api/auth/login', { username, password })
            const newToken = response.data.token;

            setToken(newToken)
            localStorage.setItem('token', newToken); // <--- Save it so the Interceptor can find it!

            setError('')
            setPassword('')
        } catch (err) {
            setError('Login failed. Check credentials.')
        }
    }

    // --- DASHBOARD ACTIONS ---

    useEffect(() => {
        if (token) fetchSecrets()
    }, [token])

    const fetchSecrets = async () => {
        try {
            // CHANGE 3: Remove the 'headers' object entirely!
            const response = await api.get('/api/credentials')
            setSecrets(response.data)
        } catch (err) {
            console.error("Failed to fetch secrets", err)
            // Optional: If error is 403/401, logout the user
            if (err.response && err.response.status === 403) setToken('');
        }
    }

    const handleAddSecret = async () => {
        try {
            // CHANGE 3 (Again): No headers needed.
            await api.post('/api/credentials',
                {
                    url: newUrl,
                    username: newUsername,
                    encryptedPassword: newPassword // Note: Send as 'password', backend encrypts it
                }
            )
            fetchSecrets()
            setNewUrl('')
            setNewUsername('')
            setNewPassword('')
        } catch (err) {
            alert("Failed to save secret")
        }
    }

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this secret?")) return
        try {
            await api.delete(`/api/credentials/${id}`)
            setSecrets(secrets.filter(secret => secret.id !== id))
        } catch (err) {
            alert("Failed to delete secret")
        }
    }

    // ... (Rest of your JSX view remains exactly the same) ...
    // NEW: Toggle visibility of a specific password
    const toggleReveal = (id) => {
        if (revealedId === id) {
            setRevealedId(null) // Hide if already open
        } else {
            setRevealedId(id) // Show this one
        }
    }

    // --- VIEW: LOGGED IN ---
    if (token) {
        return (
            <div className="app-container">
                <div className="header-row">
                    <h1>🔓 Vault Unlocked</h1>
                    <button className="logout-btn" onClick={() => {
                        setToken('');
                        localStorage.removeItem('token'); // Clear storage on logout
                    }}>Logout</button>
                </div>

                {/* Form */}
                <div className="card form-card">
                    <h3>➕ Add New Secret</h3>
                    <input
                        placeholder="Website / App Name"
                        value={newUrl}
                        onChange={e => setNewUrl(e.target.value)}
                    />
                    <input
                        placeholder="Username"
                        value={newUsername}
                        onChange={e => setNewUsername(e.target.value)}
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        value={newPassword}
                        onChange={e => setNewPassword(e.target.value)}
                    />
                    <button onClick={handleAddSecret}>Save to Vault</button>
                </div>

                {/* List */}
                <div className="secrets-grid">
                    {secrets.map((secret) => (
                        <div key={secret.id} className="card secret-card">
                            <div className="card-content">
                                <h3>{secret.url}</h3>
                                <p><strong>User:</strong> {secret.username}</p>

                                <div className="password-row">
                                    <strong>Pass:</strong>
                                    {/* LOGIC: Is this card revealed? Show text. If not, show dots. */}
                                    <span className="password-text">
                                        {/* NOTE: Make sure your backend returns 'password' or 'encryptedPassword' */}
                                        {revealedId === secret.id ? (secret.password || secret.encryptedPassword) : "••••••••••••"}
                                    </span>
                                    <button
                                        className="icon-btn"
                                        onClick={() => toggleReveal(secret.id)}
                                        title={revealedId === secret.id ? "Hide" : "Show"}
                                    >
                                        {revealedId === secret.id ? "🙈" : "👁️"}
                                    </button>
                                </div>
                            </div>

                            <button
                                className="delete-btn"
                                onClick={() => handleDelete(secret.id)}
                                title="Delete Secret"
                            >
                                🗑️
                            </button>
                        </div>
                    ))}
                </div>
            </div>
        )
    }

    // --- VIEW: LOGGED OUT ---
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