import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

function App() {
    const [token, setToken] = useState('')
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')

    // Dashboard State
    const [secrets, setSecrets] = useState([])
    const [newUrl, setNewUrl] = useState('')
    const [newUsername, setNewUsername] = useState('')
    const [newPassword, setNewPassword] = useState('')

    // NEW: Track which password is currently visible (null = none)
    const [revealedId, setRevealedId] = useState(null)

    // --- AUTHENTICATION ---
    const handleRegister = async () => {
        try {
            await axios.post('http://localhost:8080/api/auth/register', { username, password })
            setError('Registration successful! Please login.')
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed.')
        }
    }

    const handleLogin = async () => {
        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', { username, password })
            setToken(response.data.token)
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
            const response = await axios.get('http://localhost:8080/api/credentials', {
                headers: { Authorization: `Bearer ${token}` }
            })
            setSecrets(response.data)
        } catch (err) {
            console.error("Failed to fetch secrets", err)
        }
    }

    const handleAddSecret = async () => {
        try {
            await axios.post('http://localhost:8080/api/credentials',
                {
                    url: newUrl,
                    username: newUsername,
                    encryptedPassword: newPassword
                },
                { headers: { Authorization: `Bearer ${token}` } }
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
            await axios.delete(`http://localhost:8080/api/credentials/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
            setSecrets(secrets.filter(secret => secret.id !== id))
        } catch (err) {
            alert("Failed to delete secret")
        }
    }

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
                    <button className="logout-btn" onClick={() => setToken('')}>Logout</button>
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
                                        {revealedId === secret.id ? secret.encryptedPassword : "••••••••••••"}
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